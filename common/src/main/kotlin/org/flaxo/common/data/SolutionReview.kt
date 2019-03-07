package org.flaxo.common.data

/**
 * Task solution review.
 */
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
