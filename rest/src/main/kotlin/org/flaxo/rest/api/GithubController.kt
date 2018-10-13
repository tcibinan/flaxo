package org.flaxo.rest.api

import org.flaxo.git.GitPayload
import org.flaxo.git.PullRequest
import org.flaxo.github.GithubException
import org.flaxo.model.DataManager
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.response.ResponseManager
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.apache.logging.log4j.LogManager
import org.flaxo.common.ExternalService
import org.flaxo.common.GithubAuthData
import org.flaxo.rest.manager.response.Response
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
class GithubController(private val responseManager: ResponseManager,
                       private val dataManager: DataManager,
                       private val githubManager: GithubManager,
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
    fun githubAuth(principal: Principal): Response<GithubAuthData> {
        val state = Random().nextInt().toString()

        synchronized(states) { states[principal.name] = state }

        return responseManager.ok(GithubAuthData(
                redirectUrl = "$githubAuthUrl/authorize",
                requestParams = mapOf(
                        "client_id" to clientId,
                        "scope" to listOf("delete_repo", "repo").joinToString(separator = " "),
                        "state" to state
                )
        ))
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

        val githubId = githubManager.with(accessToken).nickname()

        dataManager.addGithubId(nickname, githubId)
        dataManager.addToken(nickname, ExternalService.GITHUB, accessToken)

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
        val hook: GitPayload? = githubManager.parsePayload(payloadReader, headers)

        when (hook) {
            is PullRequest -> {
                logger.info("Github pull request web hook received from ${hook.authorLogin} " +
                        "to ${hook.receiverLogin}/${hook.receiverRepositoryName}.")
                githubManager.upsertPullRequest(hook)
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
