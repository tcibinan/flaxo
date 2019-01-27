package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Plagiarism report.
 */
class PlagiarismReport(
        /**
         * Plagiarism report source url.
         */
        val url: String,

        /**
         * Plagiarism report creation date time.
         */
        val date: DateTime,

        /**
         * Plagiarism report matches.
         */
        val matches: List<PlagiarismMatch>
)
