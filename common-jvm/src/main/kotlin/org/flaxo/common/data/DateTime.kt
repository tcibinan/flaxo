package org.flaxo.common.data

import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import java.time.LocalDateTime

/**
 * JVM implementation of a multiplatform datetime.
 */
actual class DateTime(@Transient private val dateTime: LocalDateTime = LocalDateTime.now()): Comparable<DateTime> {

    /**
     * Serialization methods has to be overridden explicitly due to a serialization compiler bug.
     */
    @Serializer(forClass = DateTime::class)
    actual companion object : DateTimeSerializer() {

        override fun deserialize(decoder: Decoder): DateTime = super.deserialize(decoder)

        override fun serialize(encoder: Encoder, obj: DateTime) = super.serialize(encoder, obj)

        actual fun fromDateTimeString(string: String): DateTime = DateTime(LocalDateTime.parse(string))

        actual fun now(): DateTime = DateTime(LocalDateTime.now())

        actual fun min(): DateTime = DateTime(LocalDateTime.MIN)

        actual fun max(): DateTime = DateTime(LocalDateTime.MAX)
    }

    @JsonValue
    actual fun toDateTimeString(): String = dateTime.toString()

    actual fun toDateString(): String = toDateTimeString().substring(0, 10)

    actual override operator fun compareTo(other: DateTime): Int = dateTime.compareTo(other.dateTime)
}
