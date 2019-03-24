package org.flaxo.common

import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.time.LocalDateTime

/**
 * JVM implementation of a multiplatform datetime.
 */
@Serializable(with = DateTimeSerializer::class)
actual class DateTime(@Transient private val dateTime: LocalDateTime = LocalDateTime.now()) {

    actual companion object {

        actual fun fromDateTimeString(string: String): DateTime = DateTime(LocalDateTime.parse(string))

        actual fun now(): DateTime = DateTime(LocalDateTime.now())
    }

    @JsonValue
    actual fun toDateTimeString(): String = dateTime.toString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual operator fun compareTo(other: DateTime): Int = dateTime.compareTo(other.dateTime)
}
