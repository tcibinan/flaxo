package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.api.ServerAnswer.*
import com.tcibinan.flaxo.rest.service.response.ResponseService
import org.apache.http.client.fluent.Content
import org.apache.http.client.fluent.Form
import org.apache.http.client.fluent.Request
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/rest/github")
class GithubController(
        val responseService: ResponseService
) {

    @Value("\${GITHUB_ID}")
    lateinit var clientId: String
    @Value("\${GITHUB_SECRET}")
    lateinit var clientSecret: String
    @Value("\${GITHUB_REDIRECT}")
    lateinit var redirectUri: String
    @Value("\${HOME_PAGE}")
    lateinit var homePage: String

    @GetMapping("/auth")
    fun githubAuth(): Any {
        return responseService.response(MANUAL_REDIRECT, payload = object {
            val redirect = "http://github.com/login/oauth/authorize"
            val params = mapOf(
                    "client_id" to clientId,
                    "redirect_uri" to redirectUri,
                    "state" to Random().nextInt().toString()
            )
        })
    }

    @GetMapping("/auth/code")
    fun githubAuthToken(@RequestParam("code") code: String, @RequestParam("state") state: String) {
        val content: Content = Request.Post("https://github.com/login/oauth/access_token")
                .bodyForm(
                        Form.form().apply {
                            add("client_id", clientId)
                            add("client_secret", clientSecret)
                            add("code", code)
                            add("redirect_uri", homePage)
                            add("state", state)
                        }.build()
                )
                .execute()
                .returnContent()

        println(content)
    }
}