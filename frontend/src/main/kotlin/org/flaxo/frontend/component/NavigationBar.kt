package org.flaxo.frontend.component

import kotlinx.html.ButtonType
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import kotlinx.html.role
import org.flaxo.frontend.component.services.GITHUB_MODAL_ID
import org.flaxo.frontend.data.User
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
                +"Flaxo"
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
                        div(classes = "dropdown-menu") {
                            attrs {
                                attributes["aria-labelledby"] = NAVIGATION_BAR_SERVICES_DROPDOWN_ID
                            }
                            a(classes = "dropdown-item") {
                                attrs {
                                    attributes["data-toggle"] = "modal"
                                    attributes["data-target"] = "#$GITHUB_MODAL_ID"
                                }
                                +"Github:${props.user.isGithubAuthorized}"
                            }
                            // TODO 12.08.18: Add travis settings modal
                            a(classes = "dropdown-item") { +"Travis:${props.user.isTravisAuthorized}" }
                            // TODO 12.08.18: Add codacy settings modal
                            a(classes = "dropdown-item") { +"Codacy:${props.user.isCodacyAuthorized}" }
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
