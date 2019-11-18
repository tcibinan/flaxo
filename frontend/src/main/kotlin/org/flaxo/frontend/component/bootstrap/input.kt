package org.flaxo.frontend.component.bootstrap

import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.flaxo.frontend.component.ariaDescribedBy
import org.flaxo.frontend.component.label
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.dom.defaultValue
import react.dom.div
import react.dom.input
import react.dom.small

/**
 * Adds bootstrap text input html elements.
 */
fun RBuilder.inputComponent(inputId: String,
                            name: String,
                            description: String,
                            default: String? = null,
                            disabled: Boolean = false,
                            onUpdate: (String) -> Unit = {}) {
    val inputHelpId = "$inputId-help"
    div("form-group") {
        label(name, inputId)
        input {
            attrs {
                id = inputId
                classes = setOf("form-control")
                type = InputType.text
                if (default != null) {
                    defaultValue = default
                }
                if (disabled) {
                   this.disabled = true
                }
                ariaDescribedBy = inputHelpId
                onChangeFunction = { event -> onUpdate((event.target as HTMLInputElement).value) }
            }
        }
        small {
            attrs {
                id = inputHelpId
                classes = setOf("form-text", "text-muted")
            }
            +description
        }
    }
}
