package org.flaxo.common.data

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.StringDescriptor

/**
 * Multiplatform date time class.
 */
@Serializable(with = DateTimeSerializer::class)
expect class DateTime {

    companion object {

        /**
         * Parse date time from the string of the following format:
         * 2018-03-08T15:00:00.000
         */
        fun fromDateTimeString(string: String): DateTime

        /**
         * Returns current date time instance.
         */
        fun now(): DateTime
    }

    /**
     * Formats date time to a string of the following format:
     * 2018-03-08T15:00:00.000
     */
    fun toDateTimeString(): String

    /**
     * Formats date time to a string of the following format:
     * 2018-03-08
     */
    fun toDateString(): String

    operator fun compareTo(other: DateTime): Int
}

class DateTimeSerializer : KSerializer<DateTime> {

    override val descriptor: SerialDescriptor = StringDescriptor

    override fun deserialize(input: Decoder): DateTime = DateTime.fromDateTimeString(input.decodeString())

    override fun serialize(output: Encoder, obj: DateTime) = output.encodeString(obj.toDateTimeString())

}
