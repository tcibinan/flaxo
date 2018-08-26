package org.flaxo.rest.manager.message

import org.springframework.context.MessageSource
import java.util.*
import kotlin.NoSuchElementException

/**
 * Basic localized message service implementation.
 */
class SimpleMessageManager(private val messageSource: MessageSource) : MessageManager {

    override fun get(code: String,
                     vararg params: String,
                     locale: Locale
    ): String = try {
        messageSource.getMessage(code, params, locale)
    } catch (e: NoSuchElementException) {
        "No message provided"
    }
}