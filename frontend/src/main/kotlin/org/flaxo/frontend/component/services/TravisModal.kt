package org.flaxo.frontend.component.services

import kotlinx.html.ButtonType
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import org.flaxo.common.data.User
import react.RBuilder
import react.dom.a
import react.dom.b
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.p
import react.dom.span

const val TRAVIS_MODAL_ID = "travisModal"

fun RBuilder.travisModal(user: User) =
        div(classes = "modal fade") {
            attrs {
                id = TRAVIS_MODAL_ID
                role = "dialog"
                tabIndex = "-1"
                attributes["aria-hidden"] = "true"
            }
            div(classes = "modal-dialog") {
                attrs {
                    role = "dialog"
                    role = "document"
                }
                div(classes = "modal-content") {
                    div(classes = "modal-header") {
                        h5(classes = "modal-title") { +"Travis settings" }
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
                    div(classes = "modal-body") {
                        if (user.isTravisAuthorized) {
                            p {
                                b { +"You are already authorized with travis. " }
                                +"Flaxo retrieved travis token in background using your github token."
                            }
                        } else {
                            p {
                                +"Flaxo will retrieve travis token in background using your github token. "
                                +"The only requirement: you "
                                b { +"should be authorized " }
                                +"in "
                                a(href = "https://travis-ci.org/") { +"travis-ci" }
                                +" with your github account."
                            }
                        }
                    }
                    div(classes = "modal-footer") {
                        button(classes = "btn btn-outline-primary") {
                            attrs {
                                attributes["data-dismiss"] = "modal"
                            }
                            +"Close"
                        }
                    }
                }
            }
        }