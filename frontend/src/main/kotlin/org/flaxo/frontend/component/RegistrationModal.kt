package org.flaxo.frontend.component

import org.flaxo.frontend.data.User
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpCallException
import org.flaxo.frontend.data.Credentials
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.*

class RegistrationModalProps(var onLogin: (String, String, User) -> Unit) : RProps
class RegistrationModalState(var username: String? = null,
                             var password: String? = null) : RState

fun RBuilder.registrationModal(onLogin: (String, String, User) -> Unit) = child(RegistrationModal::class) {
    attrs.onLogin = onLogin
}

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
                attributes["aria-labelledby"] = "exampleModalLabel"
                attributes["aria-hidden"] = "true"
            }
            div("modal-dialog") {
                attrs {
                    role = "document"
                }
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Modal title"
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
                                onClickFunction = { registerUser() }
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

    private fun RBuilder.label(text: String, forInput: String) {
        label {
            attrs {
                this.htmlFor = forInput
            }
            +text
        }
    }

    private fun registerUser() {
        val username = state.username ?: throw RuntimeException("Username is not set!")
        val password = state.password ?: throw RuntimeException("Password is not set!")
        val credentials = Credentials(username, password)

        try {
            val user = flaxoClient.registerUser(credentials)
            props.onLogin(username, password, user)
            // TODO 12.08.18: Notify user that registration has finished successfully
        } catch (e: FlaxoHttpCallException) {
            console.log(e)
            // TODO 12.08.18: Notify user that registration has failed
        }
    }
}