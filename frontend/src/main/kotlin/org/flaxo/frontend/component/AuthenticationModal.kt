package org.flaxo.frontend.component

import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpCallException
import org.flaxo.common.User
import org.w3c.dom.HTMLInputElement
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.wrapper.NotificationManager
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.h5
import react.dom.input
import react.dom.small
import react.dom.span
import react.setState

fun RBuilder.authenticationModal(onLogin: (String, String, User) -> Unit) = child<AuthenticationModalProps, AuthenticationModal> {
    attrs.onLogin = onLogin
}

class AuthenticationModalProps(var onLogin: (String, String, User) -> Unit) : RProps
class AuthenticationModalState(var username: String? = null,
                               var password: String? = null) : RState

class AuthenticationModal(props: AuthenticationModalProps)
    : RComponent<AuthenticationModalProps, AuthenticationModalState>(props) {

    private val flaxoClient: FlaxoClient = Container.flaxoClient

    private companion object {
        const val AUTHORIZATION_MODAL_ID = "authorizationModal"
        const val USERNAME_INPUT_ID = "usernameAuthorization"
        const val USERNAME_INPUT_HELP_ID = "usernameAuthorizationHelp"
        const val PASSWORD_INPUT_ID = "passwordAuthorization"
        const val PASSWORD_INPUT_HELP_ID = "passwordAuthorizationHelp"
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
                            form {
                                usernameInput()
                                passwordInput()
                            }
                        }
                        div("modal-footer") {
                            button(classes = "btn btn-primary", type = ButtonType.button) {
                                attrs {
                                    onClickFunction = { authorizeUser() }
                                    attributes["data-dismiss"] = "modal"
                                }
                                +"Login"
                            }
                            button(classes = "btn btn-secondary", type = ButtonType.button) {
                                attrs {
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { username = target.value }
                    }
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
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { password = target.value }
                    }
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

    private fun authorizeUser() {
        val username = state.username ?: throw RuntimeException("Username is not set!")
        val password = state.password ?: throw RuntimeException("Password is not set!")
        val credentials = Credentials(username, password)

        try {
            val user = flaxoClient.getSelf(credentials)
            props.onLogin(username, password, user)
        } catch (e: FlaxoHttpCallException) {
            console.log(e)
            NotificationManager.error("Error occurred while authenticating in flaxo.")
        }
    }

}
