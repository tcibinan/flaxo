package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.common.User
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.validatedInputValue
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.h5
import react.dom.input
import react.dom.small
import react.dom.span

fun RBuilder.authenticationModal(onLogin: (String, String, User) -> Unit) =
        child<AuthenticationModalProps, AuthenticationModal> {
            attrs.onLogin = onLogin
        }

class AuthenticationModalProps(var onLogin: (String, String, User) -> Unit) : RProps

class AuthenticationModal(props: AuthenticationModalProps)
    : RComponent<AuthenticationModalProps, EmptyState>(props) {

    private val flaxoClient: FlaxoClient = Container.flaxoClient

    private companion object {
        const val AUTHORIZATION_MODAL_ID = "authorizationModal"
        const val USERNAME_INPUT_ID = "usernameAuthorization"
        const val USERNAME_INPUT_HELP_ID = "usernameAuthorizationHelp"
        const val PASSWORD_INPUT_ID = "passwordAuthorization"
        const val PASSWORD_INPUT_HELP_ID = "passwordAuthorizationHelp"
        const val AUTHORIZATION_MODAL_CANCEL_ID = "authorizationModalCancel"
    }

    override fun RBuilder.render() {
        span {
            button(classes = "btn btn-outline-primary", type = ButtonType.button) {
                attrs {
                    attributes["data-toggle"] = "modal"
                    attributes["data-target"] = "#$AUTHORIZATION_MODAL_ID"
                }
                +"Login"
            }
            div("modal fade") {
                attrs {
                    id = AUTHORIZATION_MODAL_ID
                    tabIndex = "-1"
                    role = "dialog"
                    attributes["aria-hidden"] = "true"
                }
                div("modal-dialog") {
                    attrs {
                        role = "document"
                    }
                    div("modal-content") {
                        div("modal-header") {
                            h5("modal-title") {
                                +"Authorize"
                            }
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
                        div("modal-body") {
                            usernameInput()
                            passwordInput()
                        }
                        div("modal-footer") {
                            button(classes = "btn btn-primary", type = ButtonType.button) {
                                attrs.onClickFunction = { launch { authorizeUser() } }
                                +"Login"
                            }
                            button(classes = "btn btn-secondary", type = ButtonType.button) {
                                attrs {
                                    id = AUTHORIZATION_MODAL_CANCEL_ID
                                    attributes["data-dismiss"] = "modal"
                                }
                                +"Cancel"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun RBuilder.usernameInput() {
        div("form-group") {
            label("Username", USERNAME_INPUT_ID)
            input {
                attrs {
                    id = USERNAME_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.text
                    attributes["aria-describedby"] = USERNAME_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = USERNAME_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Account username"
            }
        }
    }

    private fun RBuilder.passwordInput() {
        div("form-group") {
            label("Password", PASSWORD_INPUT_ID)
            input {
                attrs {
                    id = PASSWORD_INPUT_ID
                    classes = setOf("form-control")
                    type = InputType.password
                    attributes["aria-describedby"] = PASSWORD_INPUT_HELP_ID
                }
            }
            small {
                attrs {
                    id = PASSWORD_INPUT_HELP_ID
                    classes = setOf("form-text", "text-muted")
                }
                +"Account password"
            }
        }
    }

    private suspend fun authorizeUser() {
        val username = validatedInputValue(USERNAME_INPUT_ID)
        val password = validatedInputValue(PASSWORD_INPUT_ID)

        if (username != null && password != null) {
            val credentials = Credentials(username, password)

            try {
                val user = flaxoClient.getSelf(credentials)
                clickOnButton(AUTHORIZATION_MODAL_CANCEL_ID)
                props.onLogin(username, password, user)
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while authenticating in flaxo.", e)
            }
        }
    }

}
