package com.tcibinan.flaxo.rest.services

import java.util.*

interface MessageService {
    fun get(code: String, vararg params: String?, locale: Locale = Locale.US): String
}