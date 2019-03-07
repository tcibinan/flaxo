package org.flaxo.common

/**
 * JavaScript implementation of a multiplatform datetime.
 */
actual class DateTime
private constructor(private val date: kotlin.js.Date) {

    actual companion object {
        actual fun fromDateTimeString(string: String): DateTime =
                DateTime(kotlin.js.Date(string))
    }

    actual fun toDateTimeString(): String = date.toISOString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual operator fun compareTo(other: DateTime): Int = date.getTime().compareTo(other.date.getTime())

    /**
     * Converts current date time to a human readable format string.
     */
    fun toHumanReadableString(): String = this.date.toDateString()
}
