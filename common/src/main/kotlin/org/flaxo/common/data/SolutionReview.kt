package org.flaxo.common.data

import kotlinx.serialization.Serializable

/**
 * Task solution review.
 */
@Serializable
data class SolutionReview(

        /**
         * Review message.
         */
        val body: String?,

        /**
         * Review approved status.
         */
        val approved: Boolean
)
