package org.flaxo.frontend.wrapper

@JsModule("js-cookie")
@JsName("Cookies")
external class Cookies {
    companion object {
        fun get(cookie: String): String?
        fun set(cookie: String, value: String)
        fun remove(cookie: String)
    }
}