package com.tcibinan.flaxo.rest.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import java.util.*
import kotlin.NoSuchElementException

class NaiveMessageService : MessageService {

    @Autowired private lateinit var messageSource: MessageSource

    override fun get(code: String, vararg params: String, locale: Locale): String {
        return try {
            messageSource.getMessage(code, params, locale)
        } catch (e: NoSuchElementException) {
            "No message provided"
        }
    }
}