package org.flaxo.common.data

/**
 * Flaxo http response object.
 */
data class Payload<T>(

        /**
         * Response payload.
         */
        val payload: T?
)
