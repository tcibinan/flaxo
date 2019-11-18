package org.flaxo.frontend.component.bootstrap

import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.flaxo.frontend.component.ariaDescribedBy
import org.flaxo.frontend.component.label
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.div
import react.dom.input
import react.dom.small

/**
 * Adds bootstrap flag input html elements.
 */
fun RBuilder.flagComponent(inputId: String,
                           name: String,
                           description: String,
                           default: Boolean = false,
                           onUpdate: (Boolean) -> Unit = {}) {
    val inputHelpId = "$inputId-help"
    div("form-group") {
        div("form-check") {
            input {
                attrs {
                    id = inputId
                    classes = setOf("form-check-input")
                    type = InputType.checkBox
                    defaultChecked = default
                    ariaDescribedBy = inputHelpId
                    onChangeFunction = { event -> onUpdate((event.target as HTMLInputElement).checked) }
                }
            }
            label(name, inputId, classes = setOf("checkbox-label"))
        }
        small {
            attrs {
                id = inputHelpId
                classes = setOf("form-text", "text-muted", "checkbox-help")
            }
            +description
        }
    }
}
