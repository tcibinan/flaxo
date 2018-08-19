package org.flaxo.frontend.component.services

import kotlinx.html.ButtonType
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Container
import org.flaxo.frontend.component.label
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.User
import org.flaxo.frontend.wrapper.NotificationManager
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.a
import react.dom.b
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.h5
import react.dom.input
import react.dom.p
import react.dom.small
import react.dom.span
import kotlin.browser.document

const val CODACY_MODAL_ID = "codacyModal"
private const val CODACY_TOKEN_INPUT_ID = "codacyTokenInput"
private const val CODACY_TOKEN_INPUT_HELP_ID = "codacyTokenInputHelp"

fun RBuilder.codacyModal(user: User) =
        div(classes = "modal fade") {
            attrs {
                id = CODACY_MODAL_ID
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
                        h5(classes = "modal-title") { +"Codacy settings" }
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
                        if (user.isCodacyAuthorized) {
                            p { b { +"You are already authorized with codacy." } }
                        }

                        form {
                            div(classes = "form-group") {
                                label("Codacy token", forInput = CODACY_TOKEN_INPUT_ID)
                                input(classes = "form-control") {
                                    attrs {
                                        id = CODACY_TOKEN_INPUT_ID
                                        attributes["aria-describedby"] = CODACY_TOKEN_INPUT_HELP_ID
                                    }
                                }
                                small(classes = "form-text text-muted") {
                                    +"Generate api token in "
                                    a(href = "https://app.codacy.com/account/apiTokens") {
                                        +"codacy account settings "
                                    }
                                    +"and copy-paste it here."
                                }
                            }
                            button(classes = "btn btn-primary") {
                                attrs {
                                    onClickFunction = { event ->
                                        event.preventDefault()
                                        updateCodacyToken()
                                    }
                                }
                                +"Update codacy token"
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

fun updateCodacyToken() {
    val codacyToken = document.getElementById(CODACY_TOKEN_INPUT_ID)
            .let { it as HTMLInputElement }
            .value

    credentials?.also {
        try {
            Container.flaxoClient.addCodacyToken(it, codacyToken)
            NotificationManager.success("Codacy token was added to your account")
        } catch (e: Exception) {
            console.log(e)
            NotificationManager.error("Error occurred while adding codacy token")
        }
    }
}