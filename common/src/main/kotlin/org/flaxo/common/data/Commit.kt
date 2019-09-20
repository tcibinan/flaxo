package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.Identifiable

/**
 * Solution commit.
 *
 * Each commit is associated with a single pull request.
 */
@Serializable
data class Commit(

        override val id: Long,

        /**
         * Sha of the commit. Git identifier.
         */
        val sha: String,

        /**
         * Associated pull request number.
         */
        val pullRequestNumber: Int,

        /**
         * Commit creation date time.
         */
        val date: DateTime?
) : Identifiable
