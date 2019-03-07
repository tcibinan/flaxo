package org.flaxo.frontend.component

import react.RBuilder
import react.dom.label

/**
 * Adds label with [text] for input with [forInput] id.
 */
fun RBuilder.label(text: String, forInput: String) = label {
    attrs.attributes["htmlFor"] = forInput
    +text
}
