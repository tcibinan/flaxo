package org.flaxo.frontend

import org.flaxo.frontend.wrapper.Cookies

val credentials: Credentials?
    get() {
        val username = Cookies.get("username")
        val password = Cookies.get("password")
        return if (username != null && password != null) Credentials(username, password) else null
    }

class Credentials(val username: String, val password: String)