package org.flaxo.rest.api

import org.flaxo.git.GitPayload
import org.flaxo.git.PullRequest
import org.flaxo.github.GithubException
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.rest.service.git.GitService
import org.flaxo.rest.service.response.ResponseService
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.Reader
import java.security.Principal
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Github integration controller.
 *
 * Stores user's unique codes in an expiring map: [states].
 */
@RestController
@RequestMapping("/rest/github")
class GithubController(private val responseService: ResponseService,
                       private val dataService: DataService,
                       private val gitService: GitService,
                       @Value("\${GITHUB_ID}")
                       private val clientId: String,
                       @Value("\${GITHUB_SECRET}")
                       private val clientSecret: String
) {

    private val githubAuthUrl = "https://github.com/login/oauth"
    private val states: MutableMap<String, String> = PassiveExpiringMap(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
    )
    private val logger = LogManager.getLogger(GithubController::class.java)

    /**
     * Github OAuth entry point method.
     *
     * Calls by the flaxo ui and redirects browser to the github OAuth page.
     */
    @GetMapping("/auth")
    @PreAuthorize("hasAuthority('USER')")
    fun githubAuth(principal: Principal): Any {
        val state = Random().nextInt().toString()

        synchronized(states) {
            states[principal.name] = state
        }
        // TODO 19.08.18: Replace with GithubAuthData object from common module
        return responseService.ok(object {
            val redirectUrl = "$githubAuthUrl/authorize"
            val requestParams = mapOf(
                    "client_id" to clientId,
                    "scope" to listOf("delete_repo", "repo").joinToString(separator = " "),
                    "state" to state
            )
        })
    }

    /**
     * Add github access token to a user credentials
     * and redirects user browser to a home page.
     *
     * Calls github api to exchange user's code to an access token.
     */
    @GetMapping("/auth/code")
    @Transactional
    fun githubAuthToken(@RequestParam("code") code: String,
                        @RequestParam("state") state: String,
                        response: HttpServletResponse
    ) {
        val accessToken = Request.Post("$githubAuthUrl/access_token")
                .bodyForm(
                        Form.form().apply {
                            add("client_id", clientId)
                            add("client_secret", clientSecret)
                            add("code", code)
                            add("state", state)
                        }.build()
                )
                .execute()
                .returnContent()
                .asString()
                .split("&")
                .find { it.startsWith("access_token") }
                ?.split("=")
                ?.last()
                ?: throw GithubException("Access token was not received from github.")

        val nickname = synchronized(states) {
            states.filterValues { it == state }
                    .apply {
                        if (size > 1)
                            throw GithubException("Two users have the same random state for github auth.")
                    }
                    .keys
                    .first()
                    .also { states.remove(it) }
        }

        val githubId = gitService.with(accessToken).nickname()

        dataService.addGithubId(nickname, githubId)
        dataService.addToken(nickname, IntegratedService.GITHUB, accessToken)

        response.sendRedirect("/")
    }

    /**
     * Retrieves and handle github webhook.
     */
    @PostMapping("/hook")
    @Transactional
    fun webHook(request: HttpServletRequest) {
        val payloadReader: Reader = request.getParameter("payload").reader()
        val headers: Map<String, List<String>> =
                request.headerNames
                        .toList()
                        .map { it.toLowerCase() to listOf(request.getHeader(it)) }
                        .toMap()
        val hook: GitPayload? = gitService.parsePayload(payloadReader, headers)

        when (hook) {
            is PullRequest -> {
                logger.info("Github pull request web hook received from ${hook.authorId} " +
                        "to ${hook.receiverId}/${hook.receiverRepositoryName}.")

                val user = dataService.getUserByGithubId(hook.receiverId)
                        ?: throw GithubException("User with githubId ${hook.receiverId} wasn't found in database.")

                val course = dataService.getCourse(hook.receiverRepositoryName, user)
                        ?: throw GithubException("Course ${hook.receiverRepositoryName} wasn't found for user ${user.nickname}.")

                val student = course.students
                        .find { it.nickname == hook.authorId }
                        ?: dataService.addStudent(hook.authorId, course).also {
                            logger.info("Student ${it.nickname} was initialised for course ${user.nickname}/${course.name}.")
                        }

                student.solutions
                        .find { it.task.branch == hook.baseBranch }
                        ?.also {
                            logger.info("Add ${hook.lastCommitSha} commit to ${student.nickname} student solution " +
                                    "for course ${user.nickname}/${course.name}.")
                            dataService.addCommit(it, hook.lastCommitSha)
                        }
            }
            else -> {
                val message = request.inputStream
                        .bufferedReader()
                        .useLines { it.joinToString("\n") }

                logger.warn("Github custom web hook received from request: $message.")

                //do nothing
            }
        }
    }

}

