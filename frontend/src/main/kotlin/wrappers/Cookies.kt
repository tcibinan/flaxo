@JsModule("js-cookie")
@JsName("Cookies")
external class Cookies {
    companion object {
        fun set(cookie: String, value: String)
        fun remove(cookie: String)
    }
}