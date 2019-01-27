package org.flaxo.common.data

/**
 * Course task solution.
 *
 * Each task represent a relationship between a single student and a single course task.
 */
data class Solution(
        /**
         * Task name.
         */
        val task: String,

        /**
         * Author student nickname.
         */
        val student: String,

        /**
         * Solution score filled by course author.
         */
        val score: Int?,

        /**
         * Solution commits.
         *
         * It is not a list of all commits but a list of the latest
         * commits in each push operation to a pull request.
         *
         * The reason why it is so is that travis builds only the latest pushed commits.
         */
        val commits: List<Commit>,

        /**
         * Solution build reports.
         */
        val buildReports: List<BuildReport>,

        /**
         * Solution code style reports.
         */
        val codeStyleReports: List<CodeStyleReport>,

        /**
         * Solution approval status.
         *
         * If set to true than solution score is frozen in its current state.
         */
        val approved: Boolean
)
