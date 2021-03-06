package org.flaxo.frontend.component

import kotlinx.html.ButtonType
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.role
import kotlinx.html.tabIndex
import react.RBuilder
import react.RComponent
import react.dom.button
import react.dom.div
import react.dom.h5
import react.dom.iframe
import react.dom.span

const val PLAGIARISM_MODAL_ID = "plagiarismModal"
const val PLAGIARISM_IFRAME_ID = "plagiarismIframe"

/**
 * Adds plagiarism visualization modal.
 */
fun RBuilder.plagiarismModal() = child(PlagiarismModal::class) { }

private class PlagiarismModal(props: EmptyProps) : RComponent<EmptyProps, EmptyState>(props) {

    override fun RBuilder.render() {
        div("modal fade plagiarism-modal") {
            attrs {
                id = PLAGIARISM_MODAL_ID
                tabIndex = "-1"
                role = "dialog"
                ariaHidden = true
            }
            div("modal-dialog modal-lg modal-full-screen") {
                attrs.role = "document"
                div("modal-content") {
                    div("modal-header") {
                        h5("modal-title") {
                            +"Plagiarism graph"
                        }
                        button(classes = "close", type = ButtonType.button) {
                            attrs {
                                dataDismiss = "modal"
                                ariaLabel = "Close"
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
                        iframe {
                            attrs {
                                id = PLAGIARISM_IFRAME_ID
                            }
                        }
                    }
                    div("modal-footer") {
                        button(classes = "btn btn-secondary", type = ButtonType.button) {
                            attrs {
                                dataDismiss = "modal"
                            }
                            +"Close"
                        }
                    }
                }
            }
        }
    }

}