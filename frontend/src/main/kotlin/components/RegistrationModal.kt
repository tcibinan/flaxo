import components.Account
import kotlinx.html.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*

class RegistrationModalProps(var onLogin: (String, String, Account) -> Unit) : RProps
class RegistrationModalState(var username: String? = null,
                             var password: String? = null) : RState

fun RBuilder.registrationModal(onLogin: (String, String, Account) -> Unit) = child(RegistrationModal::class) {
    attrs.onLogin = onLogin
}

class RegistrationModal(props: RegistrationModalProps)
    : RComponent<RegistrationModalProps, RegistrationModalState>(props) {

    override fun RBuilder.render() {
        span {
            button(classes = "btn btn-primary", type = ButtonType.button) {
            attrs {
                attributes["data-toggle"] = "modal"
                attributes["data-target"] = "#exampleModal"
            }
            +"Register"
        }
        }
        div("modal fade") {
            attrs {
                id = "exampleModal"
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
                        primaryButton("Save changes", onClick = { event ->
                            event.preventDefault()
                            registerUser()
                        })
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

    fun RBuilder.primaryButton(text: String, onClick: (Event) -> Unit = {}) {
        bootstrapButton(text, "btn btn-primary", onClick)
    }

    fun RBuilder.bootstrapButton(text: String, classes: String, onClick: (Event) -> Unit) {
        button(classes = classes, type = ButtonType.button) {
            attrs {
                onClickFunction = onClick
            }
            +text
        }
    }

    fun RBuilder.usernameInput() {
        div("form-group") {
            label("Username", "username-registration")
            input(classes = "form-control", type = InputType.text) {
                attrs {
                    id = "username-registration"
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { username = target.value }
                    }
                }
            }
        }
    }

    fun RBuilder.passwordInput() {
        div("form-group") {
            label("Password", "password-registration")
            input(classes = "form-control", type = InputType.text) {
                attrs {
                    id = "password-registration"
                    onChangeFunction = { event ->
                        val target = event.target as HTMLInputElement
                        setState { password = target.value }
                    }
                }
            }
        }
    }

    fun RBuilder.label(text: String, forInput: String) {
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

        // TODO: Replace with an actual implementation
        val returnFromRequestAccount = Account(username, password)
        props.onLogin(username, password, returnFromRequestAccount)
//        axios
//                .post('/user/register', {}, {
//                    params: {
//                    nickname: this.state.username,
//                    password: this.state.password
//                },
//                    baseURL: restUrl()
//                })
//                .then(() => {
//                    Api.retrieveAccount({
//                        username: this.state.username,
//                        password: this.state.password
//                    },
//                            account => {
//                        this.props.onLogin(this.state.username, this.state.password, account);
//
//                        ReactDOM.render(
//                                < Notification succeed message ="Registration has finished successful."/>,
//                        document.getElementById('notifications')
//                        );
//                    },
//                    response => ReactDOM.render(
//                    <Notification message ={ `Retrieving user after registration failed due to: response` } / >,
//                    document.getElementById('notifications')
//                    )
//                    );
//                })
//        .catch(
//            response => ReactDOM . render (
//        <Notification message ={ `Registration failed due to: response` } / >,
//        document.getElementById('notifications')
//        )
//        );
    }
}