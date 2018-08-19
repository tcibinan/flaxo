package org.flaxo.frontend.component

import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*
import org.flaxo.frontend.data.User

fun RBuilder.welcomeDesk(onLogin: (String, String, User) -> Unit) = child(WelcomeDesk::class) {
    attrs.onLogin = onLogin
}

class WelcomeDeskProps(var onLogin: (String, String, User) -> Unit) : RProps

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
            div("lead") {
                registrationModal(props.onLogin)
                +" "
                authenticationModal(props.onLogin)
            }
        }

    }
}