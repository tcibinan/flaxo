package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import org.flaxo.common.User
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpCallException
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.Notifications
import org.w3c.dom.HTMLInputElement
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

fun RBuilder.registrationModal(onLogin: (String, String, User) -> Unit) = child(RegistrationModal::class) {
    attrs.onLogin = onLogin
}

class RegistrationModalProps(var onLogin: (String, String, User) -> Unit) : RProps
class RegistrationModalState(var username: String? = null,
                             var password: String? = null) : RState

class RegistrationModal(props: RegistrationModalProps)
    : RComponent<RegistrationModalProps, RegistrationModalState>(props) {

    private companion object {
        const val REGISTRATION_MODAL_ID = "registrationModal"
        const val USERNAME_INPUT_ID = "usernameRegistration"
        const val USERNAME_INPUT_HELP_ID = "usernameRegistrationHelp"
        const val PASSWORD_INPUT_ID = "passwordRegistration"
        const val PASSWORD_INPUT_HELP_ID = "passwordRegistrationHelp"
    }

    private val flaxoClient: FlaxoClient = Container.flaxoClient

    override fun RBuilder.render() {
        span {
            button(classes = "btn btn-primary", type = ButtonType.button) {
                attrs {
                    attributes["data-toggle"] = "modal"
                    attributes["data-target"] = "#$REGISTRATION_MODAL_ID"
                }
                +"Register"
            }
        }
        div("modal fade") {
            attrs {
                id = REGISTRATION_MODAL_ID
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
                            +"Register"
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
                                onClickFunction = { launch { registerUser() } }
                                attributes["data-dismiss"] = "modal"
                            }
                            +"Register"
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

    private suspend fun registerUser() {
        val username = state.username ?: throw RuntimeException("Username is not set!")
        val password = state.password ?: throw RuntimeException("Password is not set!")
        val credentials = Credentials(username, password)

        try {
            val user = flaxoClient.registerUser(credentials)
            props.onLogin(username, password, user)
            Notifications.success("User $username has been registered.")
        } catch (e: FlaxoHttpCallException) {
            console.log(e)
            Notifications.error("Error occurred while registering $username user.")
        }
    }
}