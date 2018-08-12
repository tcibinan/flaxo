package org.flaxo.frontend.component

import courses
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpCallException
import org.flaxo.frontend.credentials
import react.*
import react.dom.div
import org.flaxo.frontend.wrapper.Cookies
import org.flaxo.frontend.data.User

class PageState(var user: User? = null): RState

fun RBuilder.rootPage() = child(Page::class) {}

class Page: RComponent<EmptyProps, PageState>(EmptyProps()) {

    private companion object {
        const val USERNAME_COOKIE: String = "username"
        const val PASSWORD_COOKIE: String = "password"
    }

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
        credentials?.also {
            try {
                val user = flaxoClient.getSelf(it)
                state.user = user
            } catch (e: FlaxoHttpCallException) {
                console.log(e)
                // TODO 12.08.18: Notify user that user retrieving failed
            }
        }
    }

    override fun RBuilder.render() {
        val user = state.user
        if (user != null) {
            div("page") {
                navigationBar(user, ::onLogout)
                courses(user)
            }
        } else {
            div("page") {
                welcomeDesk(::onLogin)
            }
        }
    }

    private fun onLogin(username: String, password: String, retrievedUser: User) {
        Cookies.set(USERNAME_COOKIE, username)
        Cookies.set(PASSWORD_COOKIE, password)

        setState { user = retrievedUser }
    }

    private fun onLogout() {
        Cookies.remove(USERNAME_COOKIE)
        Cookies.remove(PASSWORD_COOKIE)

        setState { user = null }
    }
}