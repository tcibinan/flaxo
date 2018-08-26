package org.flaxo.common

/**
 * Plagiarism match.
 *
 * It is a detected plagiarism match between two students solutions.
 */
class PlagiarismMatch(
        /**
         * Plagiarism match source url.
         */
        val url: String,

        val student1: String,

        val student2: String,

        /**
         * Number of matched lines between two solutions.
         */
        val lines: Int,

        /**
         * Percentage of matched lines between two solutions.
         */
        val percentage: Int
)