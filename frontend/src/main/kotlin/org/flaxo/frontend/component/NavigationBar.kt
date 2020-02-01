package org.flaxo.frontend.component

import kotlinx.html.ButtonType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import org.flaxo.common.data.User
import org.flaxo.frontend.Configuration
import org.flaxo.frontend.component.services.CODACY_MODAL_ID
import org.flaxo.frontend.component.services.GITHUB_MODAL_ID
import org.flaxo.frontend.component.services.TRAVIS_MODAL_ID
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div
import react.dom.form
import react.dom.i
import react.dom.li
import react.dom.nav
import react.dom.span
import react.dom.ul

/**
 * Adds navigation bar.
 */
fun RBuilder.navigationBar(user: User, onLogout: () -> Unit, toCourses: () -> Unit) =
        child(NavigationBar::class) {
            attrs {
                this.user = user
                this.onLogout = onLogout
                this.toCourses = toCourses
            }
        }

private class NavigationBarProps(var user: User,
                                 var toCourses: () -> Unit,
                                 var onLogout: () -> Unit
) : RProps

private class NavigationBar(props: NavigationBarProps) : RComponent<NavigationBarProps, EmptyState>(props) {

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
                                    classes = if (props.user.githubAuthorized) setOf("dropdown-item")
                                    else setOf("dropdown-item", "pending-service")
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$GITHUB_MODAL_ID"
                                }
                                span { +"Github" }
                                if (!props.user.githubAuthorized) {
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
                                    classes = if (props.user.codacyAuthorized) setOf("dropdown-item")
                                    else setOf("dropdown-item", "pending-service")
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$CODACY_MODAL_ID"
                                }
                                span { +"Codacy" }
                                if (!props.user.codacyAuthorized) {
                                    i(classes = "material-icons pending-service-label") { +"radio_button_unchecked" }
                                }
                            }
                        }
                    }
                }
                a(classes = "nav-link") { +"Signed as ${props.user.name}" }
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
