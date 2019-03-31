package org.flaxo.frontend.component.bootstrap

import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import org.flaxo.frontend.component.ariaDescribedBy
import org.flaxo.frontend.component.label
import org.w3c.dom.HTMLSelectElement
import react.RBuilder
import react.dom.div
import react.dom.option
import react.dom.select
import react.dom.small

/**
 * Adds bootstrap select html elements.
 */
fun RBuilder.selectComponent(selectId: String,
                             name: String? = null,
                             description: String = "",
                             default: String? = null,
                             options: List<String> = emptyList(),
                             emptyOption: Boolean = false,
                             onUpdate: (String) -> Unit = {}) {
    val selectorHelpId = "$selectId-help"
    div("form-group") {
        if (name != null) label(name, selectId)
        select {
            attrs {
                id = selectId
                classes = setOf("form-control")
                onChangeFunction = { event -> onUpdate((event.target as HTMLSelectElement).value) }
                ariaDescribedBy = selectorHelpId
            }
            if (emptyOption) option { attrs.selected = default == null }
            options.forEach {
                option {
                    attrs {
                        value = it
                        selected = it == default
                    }
                    +it
                }
            }
        }
        small {
            attrs {
                id = selectorHelpId
                classes = setOf("form-text", "text-muted")
            }
            +description
        }
    }
}
