package org.flaxo.rest.service.message

import java.util.*

/**
 * Flaxo localized message service.
 */
interface MessageService {

    /**
     * Returns a message by the given [code] for the needed [locale] passing
     * [params] as message placeholders.
     */
    fun get(code: String,
            vararg params: String = emptyArray(),
            locale: Locale = Locale.US
    ): String
}