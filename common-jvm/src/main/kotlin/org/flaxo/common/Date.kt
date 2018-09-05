package org.flaxo.common

import com.fasterxml.jackson.annotation.JsonValue
import java.time.LocalDateTime

actual class DateTime(private val dateTime: LocalDateTime) {

    actual companion object {
        actual fun fromDateTimeString(string: String): DateTime =
                DateTime(LocalDateTime.parse(string))
    }

    @JsonValue
    actual fun toDateTimeString(): String = dateTime.toString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual operator fun compareTo(other: DateTime): Int =
            dateTime.compareTo(other.dateTime)
}
