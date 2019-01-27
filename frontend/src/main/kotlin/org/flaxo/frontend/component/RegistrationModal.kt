package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.hidden
import kotlinx.html.id
import org.flaxo.common.data.User
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.Credentials
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.clickOnButton
import org.flaxo.frontend.validatedInputValue
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.input
import react.dom.small
import react.dom.span

fun RBuilder.registrationModal(onLogin: (String, String, User) -> Unit) = child(RegistrationModal::class) {
    attrs.onLogin = onLogin
}

class RegistrationModalProps(var onLogin: (String, String, User) -> Unit) : RProps

class RegistrationModal(props: RegistrationModalProps)
    : RComponent<RegistrationModalProps, EmptyState>(props) {

    private companion object {
        const val REGISTRATION_MODAL_ID = "registrationModal"
        const val USERNAME_INPUT_ID = "usernameRegistration"
        const val USERNAME_INPUT_HELP_ID = "usernameRegistrationHelp"
        const val PASSWORD_INPUT_ID = "passwordRegistration"
        const val PASSWORD_INPUT_HELP_ID = "passwordRegistrationHelp"
        const val REGISTRATION_MODAL_CANCEL_ID = "registrationModelCancel"
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
                        usernameInput()
                        passwordInput()
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-primary", type = ButtonType.button) {
                            attrs.onClickFunction = { launch { registerUser() } }
                            +"Register"
                        }
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                id = REGISTRATION_MODAL_CANCEL_ID
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

    private suspend fun registerUser() {
        val username = validatedInputValue(USERNAME_INPUT_ID)
        val password = validatedInputValue(PASSWORD_INPUT_ID)

        if (username != null && password != null) {
            val credentials = Credentials(username, password)

            try {
                val user = flaxoClient.registerUser(credentials)
                clickOnButton(REGISTRATION_MODAL_CANCEL_ID)
                props.onLogin(username, password, user)
                Notifications.success("User $username has been registered.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while registering $username user.", e)
            }
        }
    }
}
