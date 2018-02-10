package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.IntegratedService
import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.service.response.ResponseService
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.concurrent.Executors

@RestController
@RequestMapping("/rest/github")
class GithubController(
        private val responseService: ResponseService,
        private val dataService: DataService
) {

    val githubAuthUrl = "https://github.com/login/oauth"
    @Value("\${GITHUB_ID}")
    lateinit var clientId: String
    @Value("\${GITHUB_SECRET}")
    lateinit var clientSecret: String

    @GetMapping("/auth")
    fun githubAuth(): Any {
        return responseService.response(MANUAL_REDIRECT, payload = object {
            val redirect = "$githubAuthUrl/authorize"
            val params = mapOf(
                    "client_id" to clientId,
                    "scope" to listOf("delete_repo", "repo").joinToString(separator = " ")
            )
        })
    }

    @GetMapping("/auth/code")
    fun githubAuthToken(@RequestParam("code") code: String,
                        @RequestParam("state") state: String,
                        principal: Principal
    ) {
        Executors.newSingleThreadExecutor().execute {
            Thread.sleep(2000)
            val accessToken = Request.Post("$githubAuthUrl/access_token")
                    .bodyForm(
                            Form.form().apply {
                                add("client_id", clientId)
                                add("client_secret", clientSecret)
                                add("code", code)
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

            dataService.addToken(principal.name, IntegratedService.GITHUB, accessToken)
        }
    }
}