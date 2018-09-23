package org.flaxo.frontend.component.services

import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.User
import org.flaxo.frontend.github.githubProfileUrl
import org.flaxo.frontend.Notifications
import org.w3c.dom.url.URLSearchParams
import react.RBuilder
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.p
import react.dom.span
import kotlin.browser.window

const val GITHUB_MODAL_ID = "githubModal"

fun RBuilder.githubModal(user: User) =
        div(classes = "modal fade") {
            attrs {
                id = GITHUB_MODAL_ID
                role = "dialog"
                tabIndex = "-1"
                attributes["aria-hidden"] = "true"
            }
            div(classes = "modal-dialog") {
                attrs {
                    role = "dialog"
                    role = "document"
                }
                div(classes = "modal-content") {
                    div(classes = "modal-header") {
                        h5(classes = "modal-title") { +"Github settings" }
                        button(classes = "close", type = ButtonType.button) {
                            attrs {
                                attributes["data-dismiss"] = "modal"
                                attributes["aria-label"] = "Close"
                            }
                            span {
                                attrs {
                                    hidden = true
                                }
                                +"&times;"
                            }
                        }
                    }
                    div(classes = "modal-body") {
                        if (user.isGithubAuthorized) {
                            p {
                                +"Your github account is "
                                a(href = user.githubId?.let { githubProfileUrl(it) } ?: "") {
                                    +(user.githubId ?: "undefined")
                                }
                                +"."
                            }
                            button(classes = "btn btn-secondary") {
                                attrs {
                                    disabled = true
                                }
                                +"Logout from Github"
                            }
                        } else {
                            button(classes = "btn btn-primary") {
                                attrs {
                                    onClickFunction = { launch { authWithGithub() } }
                                }
                                +"Sing in with Github"
                            }
                        }
                    }
                    div(classes = "modal-footer") {
                        button(classes = "btn btn-outline-primary") {
                            attrs {
                                attributes["data-dismiss"] = "modal"
                            }
                            +"Close"
                        }
                    }
                }
            }
        }

private suspend fun authWithGithub() {
    try {
        credentials?.also {
            val githubAuthData = Container.flaxoClient.getGithubAuthData(it)
            val params = URLSearchParams()
            githubAuthData.requestParams.forEach { (key, value) -> params.append(key, value) }
            window.location.assign(githubAuthData.redirectUrl + "?" + params.toString())
        }
    } catch (e: Exception) {
        console.log(e)
        Notifications.error("Error occurred while trying to authenticate with github.")
    }
}
