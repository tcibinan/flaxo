package org.flaxo.frontend.component

import react.RBuilder
import react.dom.label

fun RBuilder.label(text: String, forInput: String) = label {
    attrs.attributes["htmlFor"] = forInput
    +text
}
