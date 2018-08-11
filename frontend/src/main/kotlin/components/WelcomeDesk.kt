package components

import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*
import EmptyState
import registrationModal

class WelcomeDeskProps(var onLogin: (String, String, Account) -> Unit) : RProps

fun RBuilder.welcomeDesk(onLogin: (String, String, Account) -> Unit) = child(WelcomeDesk::class) {
    attrs.onLogin = onLogin
}

class WelcomeDesk : RComponent<WelcomeDeskProps, EmptyState>() {

    override fun RBuilder.render() {
        div("jumbotron") {
            h1("display-3") { +"Flaxo" }
            p("lead") { +"An open git - based educational platform for everyone" }
            hr("my-2") {}
            p {
                +("Flaxo tests and assess students solutions for you. " +
                        "It can even search for plagiarism. And it is completely free.")
            }
            p("lead") {
                registrationModal(props.onLogin)
                +" "
//                authenticationModal(props.onLogin)
            }
        }

    }
}