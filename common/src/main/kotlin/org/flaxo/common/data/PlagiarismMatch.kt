package org.flaxo.common.data

/**
 * Plagiarism match.
 *
 * It is a detected plagiarism match between two students solutions.
 */
data class PlagiarismMatch(

        override val id: Long,

        /**
         * Plagiarism match source url.
         */
        val url: String,

        /**
         * First of the plagiarism match students.
         */
        val student1: String,

        /**
         * Second of the plagiarism match students.
         */
        val student2: String,

        /**
         * Number of matched lines between two solutions.
         */
        val lines: Int,

        /**
         * Percentage of matched lines between two solutions.
         */
        val percentage: Int
): Identifiable
