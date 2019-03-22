package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Solution commit.
 *
 * Each commit is associated with a single pull request.
 */
data class Commit(

        override val id: Long,

        /**
         * Sha of the commit. Git identifier
         */
        val sha: String,

        /**
         * Associated pull request id.
         *
         * TODO: The only reason the field is nullable is a deployment purposes.
         */
        val pullRequestId: Int?,

        /**
         * Commit creation date time.
         */
        val date: DateTime?
) : Identifiable
