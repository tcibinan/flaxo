package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.DateTime

/**
 * Plagiarism report.
 */
@Serializable
data class PlagiarismReport(

        override val id: Long,

        override val date: DateTime,

        /**
         * Plagiarism report source url.
         */
        val url: String,

        /**
         * Plagiarism report matches.
         */
        val matches: List<PlagiarismMatch>
) : Identifiable, Dated
