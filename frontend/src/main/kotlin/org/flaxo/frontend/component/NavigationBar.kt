package org.flaxo.frontend.component

import kotlinx.html.ButtonType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import org.flaxo.frontend.component.services.CODACY_MODAL_ID
import org.flaxo.frontend.component.services.GITHUB_MODAL_ID
import org.flaxo.frontend.component.services.TRAVIS_MODAL_ID
import org.flaxo.frontend.Configuration
import org.flaxo.common.User
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.*

fun RBuilder.navigationBar(user: User, onLogout: () -> Unit, toCourses: () -> Unit) =
        child(NavigationBar::class) {
            attrs {
                this.user = user
                this.onLogout = onLogout
                this.toCourses = toCourses
            }
        }

class NavigationBarProps(var user: User,
                         var toCourses: () -> Unit,
                         var onLogout: () -> Unit) : RProps

class NavigationBar(props: NavigationBarProps) : RComponent<NavigationBarProps, EmptyState>(props) {

    private companion object {
        const val NAVIGATION_BAR_COLLAPSIBLE_ID = "navbarSupportedContent"
        const val NAVIGATION_BAR_SERVICES_DROPDOWN_ID = "navbarDropdown"
    }

    override fun RBuilder.render() {
        nav(classes = "navbar navbar-expand-sm navbar-light bg-light") {
            a(classes = "navbar-brand", href = "#") {
                attrs {
                    onClickFunction = { props.toCourses() }
                }
                +"Flaxo "
                span(classes = "text-muted align-middle flaxo-version") {
                    +"v"
                    +Configuration.FLAXO_VERSION
                }
            }
            button(classes = "navbar-toggler", type = ButtonType.button) {
                attrs {
                    attributes["data-toggle"] = "collapse"
                    attributes["data-target"] = "#$NAVIGATION_BAR_COLLAPSIBLE_ID"
                    attributes["aria-controls"] = NAVIGATION_BAR_COLLAPSIBLE_ID
                    attributes["aria-expanded"] = "false"
                    attributes["aria-label"] = "Toggle navigation"
                }
                span("navbar-toggler-icon") { }
            }
            div(classes = "collapse navbar-collapse") {
                attrs { id = NAVIGATION_BAR_COLLAPSIBLE_ID }
                ul(classes = "navbar-nav mr-auto") {
                    li(classes = "nav-item active") {
                        a(classes = "nav-link", href = "#courses") {
                            attrs {
                                onClickFunction = { props.toCourses() }
                            }
                            +"Courses"
                        }
                    }
                    li(classes = "nav-item dropdown") {
                        a(classes = "nav-link dropdown-toggle", href = "#") {
                            attrs {
                                id = NAVIGATION_BAR_SERVICES_DROPDOWN_ID
                                role = "button"
                                attributes["data-toggle"] = "dropdown"
                                attributes["aria-haspopup"] = "true"
                                attributes["aria-expanded"] = "false"
                            }
                            +"Services"
                        }
                        div(classes = "dropdown-menu services-list") {
                            attrs {
                                attributes["aria-labelledby"] = NAVIGATION_BAR_SERVICES_DROPDOWN_ID
                            }
                            a(href = "#") {
                                attrs {
                                    classes = if (props.user.isGithubAuthorized) setOf("dropdown-item")
                                    else setOf("dropdown-item", "pending-service")
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$GITHUB_MODAL_ID"
                                }
                                span { +"Github" }
                                if (!props.user.isGithubAuthorized) {
                                    i(classes = "material-icons pending-service-label") { +"radio_button_unchecked" }
                                }
                            }
                            a(href = "#") {
                                attrs {
                                    classes = setOf("dropdown-item")
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$TRAVIS_MODAL_ID"
                                }
                                span { +"Travis" }
                            }
                            a(href = "#") {
                                attrs {
                                    classes = if (props.user.isCodacyAuthorized) setOf("dropdown-item")
                                    else setOf("dropdown-item", "pending-service")
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$CODACY_MODAL_ID"
                                }
                                span { +"Codacy" }
                                if (!props.user.isCodacyAuthorized) {
                                    i(classes = "material-icons pending-service-label") { +"radio_button_unchecked" }
                                }
                            }
                        }
                    }
                }
                a(classes = "nav-link") { +"Signed as ${props.user.nickname}" }
                form(classes = "form-inline my-2 my-lg-0") {
                    button(classes = "btn btn-outline-secondary my-2 my-sm-0", type = ButtonType.submit) {
                        attrs { onClickFunction = { props.onLogout() } }
                        +"Logout"
                    }
                }
            }
        }
    }

}
