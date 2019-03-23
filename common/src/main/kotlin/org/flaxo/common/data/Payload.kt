package org.flaxo.common.data

import kotlinx.serialization.Serializable

/**
 * Flaxo http response object.
 */
@Serializable
@Deprecated("Flatten the use of the class with the contents of its payload field.")
data class Payload<T>(

        /**
         * Response payload.
         */
        val payload: T?
)
