package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.IntegratedService
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.service.response.ResponseService
import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.*
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/rest/github")
class GithubController(
        private val responseService: ResponseService,
        private val dataService: DataService
) {

    val githubAuthUrl = "https://github.com/login/oauth"
    val states: MutableMap<String, String> = PassiveExpiringMap(
            TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES)
    )
    @Value("\${GITHUB_ID}")
    lateinit var clientId: String
    @Value("\${GITHUB_SECRET}")
    lateinit var clientSecret: String

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

        dataService.addToken(nickname, IntegratedService.GITHUB, accessToken)

        response.sendRedirect("/")
    }
}