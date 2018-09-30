package org.flaxo.frontend.wrapper

@JsName("$")
external fun jquery(selector: String): JQuery

external class JQuery {
    fun modal(action: String)
}
