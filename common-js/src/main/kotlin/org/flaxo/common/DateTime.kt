package org.flaxo.common

import kotlin.math.floor
import kotlin.math.roundToInt

private const val MILLIS_IN_DAY = 1000 * 60 * 60 * 24

/**
 * JavaScript implementation of a multiplatform datetime.
 */
actual class DateTime private constructor(private val date: kotlin.js.Date) {

    actual companion object {

        actual fun fromDateTimeString(string: String): DateTime = DateTime(kotlin.js.Date(string))

        actual fun now(): DateTime = DateTime(kotlin.js.Date())
    }

    actual fun toDateTimeString(): String = date.toISOString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual operator fun compareTo(other: DateTime): Int = date.getTime().compareTo(other.date.getTime())

    /**
     * Returns a number of days between this and the [other] datetime.
     */
    fun daysUntil(other: DateTime): Int =
            floor(((other.date.getTime() - this.date.getTime()) / MILLIS_IN_DAY)).roundToInt()

    /**
     * Converts current date time to a human readable format string.
     */
    fun toHumanReadableString(): String = date.toDateString()
}
