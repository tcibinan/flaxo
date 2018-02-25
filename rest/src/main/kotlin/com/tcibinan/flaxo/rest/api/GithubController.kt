package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.git.GitPayload
import com.tcibinan.flaxo.git.PullRequest
import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.IntegratedService
import com.tcibinan.flaxo.rest.api.ServerAnswer.MANUAL_REDIRECT
import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.response.ResponseService
import com.tcibinan.flaxo.rest.service.travis.TravisService
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.Reader
import java.security.Principal
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rest/github")
class GithubController(
        private val responseService: ResponseService,
        private val dataService: DataService,
        private val travisService: TravisService,
        private val gitService: GitService
) {

    private val githubAuthUrl = "https://github.com/login/oauth"
    private val states: MutableMap<String, String> = PassiveExpiringMap(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
    )
    private val logger = LogManager.getLogger(GithubController::class.java)
    @Value("\${GITHUB_ID}")
    private lateinit var clientId: String
    @Value("\${GITHUB_SECRET}")
    private lateinit var clientSecret: String

    @GetMapping("/auth")
    @PreAuthorize("hasAuthority('USER')")
    fun githubAuth(principal: Principal): Any {
        val state = Random().nextInt().toString()

        synchronized(states) {
            states[principal.name] = state
        }

        return responseService.response(MANUAL_REDIRECT, payload = object {
            val redirect = "$githubAuthUrl/authorize"
            val params = mapOf(
                    "client_id" to clientId,
                    "scope" to listOf("delete_repo", "repo").joinToString(separator = " "),
                    "state" to state
            )
        })
    }

    @GetMapping("/auth/code")
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
                ?: throw Exception("Access token was not received from github.")

        val nickname = synchronized(states) {
            val key = states.filterValues { it == state }
                    .apply {
                        if (size > 1)
                            throw Exception("Two users have the same random state for github auth.")
                    }
                    .keys.first()

            states.remove(key)
            key
        }

        val githubId = gitService.with(accessToken).nickname()

        dataService.addGithubId(nickname, githubId)
        dataService.addToken(nickname, IntegratedService.GITHUB, accessToken)

        response.sendRedirect("/")
    }

    @PostMapping("/hook")
    fun webHook(requestEntity: HttpEntity<ByteArray>) {
        val bodyReader: Reader = requestEntity.body.inputStream().reader()
        val headers: Map<String, List<String>> = requestEntity.headers.toMap()
        val hook: GitPayload? = gitService.parsePayload(bodyReader, headers)

        when (hook) {
            is PullRequest -> {
                if (hook.isOpened) {
                    logger.info("Github opening pull request web hook received from ${hook.authorId} " +
                            "to ${hook.receiverId}/${hook.receiverRepositoryName}")

                    val courseAuthor = dataService.getUserByGithubId(hook.receiverId)
                            ?: throw Exception("User with githubId ${hook.receiverId} wasn't found in database.")

                    val course = dataService.getCourse(hook.receiverRepositoryName, courseAuthor)
                            ?: throw Exception("Course ${hook.receiverRepositoryName} wasn't found for user ${courseAuthor.nickname}.")

                    dataService.addStudent(hook.authorId, course)
                } else {
                    logger.info("Github updating pull request web hook received from ${hook.authorId} " +
                            "to ${hook.receiverId}/${hook.receiverRepositoryName}.")

                    // do nothing
                }
            }
            else -> {
                logger.info("Github custom web hook received from request: $requestEntity.")

                //do nothing
            }
        }
    }

}

