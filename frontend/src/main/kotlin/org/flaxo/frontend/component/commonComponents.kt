package org.flaxo.frontend.component

import kotlinx.html.classes
import react.RBuilder
import react.dom.label

/**
 * Adds label with [text] for input with [forInput] id.
 */
fun RBuilder.label(text: String, forInput: String, classes : Set<String> = emptySet()) = label {
    attrs {
        this.attributes["htmlFor"] = forInput
        this.classes = classes
    }
    +text
}
