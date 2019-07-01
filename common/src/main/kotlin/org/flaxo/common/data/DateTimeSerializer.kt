package org.flaxo.common.data

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.internal.StringDescriptor

open class DateTimeSerializer : KSerializer<DateTime> {

    override val descriptor: SerialDescriptor = StringDescriptor

    override fun deserialize(decoder: Decoder): DateTime = DateTime.fromDateTimeString(decoder.decodeString())

    override fun serialize(encoder: Encoder, obj: DateTime) = encoder.encodeString(obj.toDateTimeString())

}
