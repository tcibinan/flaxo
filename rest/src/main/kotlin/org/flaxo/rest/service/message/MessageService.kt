package org.flaxo.rest.service.message

import java.util.*

interface MessageService {
    fun get(code: String, vararg params: String?, locale: Locale = Locale.US): String
}