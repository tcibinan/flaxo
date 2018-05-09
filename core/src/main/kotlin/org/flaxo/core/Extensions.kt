package org.flaxo.core

import java.util.concurrent.TimeUnit
import java.io.PrintWriter
import java.io.StringWriter


/**
 * Converts [this] of [units] to millis.
 */
infix fun Number.of(units: TimeUnit): Long =
        TimeUnit.MILLISECONDS.convert(this.toLong(), units)

/**
 * Retrieves [Throwable] stacktrace as a formatted string.
 */
fun Throwable.stringStackTrace(): String =
        StringWriter()
                .apply {
                    use { printStackTrace(PrintWriter(it)) }
                }
                .toString()