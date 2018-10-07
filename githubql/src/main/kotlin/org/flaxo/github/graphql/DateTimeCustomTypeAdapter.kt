package org.flaxo.github.graphql

import com.apollographql.apollo.response.CustomTypeAdapter
import com.apollographql.apollo.response.CustomTypeValue
import java.time.ZonedDateTime

internal class DateTimeCustomTypeAdapter : CustomTypeAdapter<ZonedDateTime> {

    override fun encode(value: ZonedDateTime): CustomTypeValue<Any> = CustomTypeValue.fromRawValue(value)

    override fun decode(value: CustomTypeValue<Any>): ZonedDateTime =
            ZonedDateTime.parse(value.value as String)

}
