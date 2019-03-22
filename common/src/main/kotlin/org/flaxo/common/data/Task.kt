package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Course task.
 *
 * Each task is associated with a git branch.
 */
data class Task(

        override val id: Long,

        /**
         * Associated git branch name.
         */
        val branch: String,

        override val name: String = branch,

        /**
         * Task deadline.
         */
        val deadline: DateTime?,

        /**
         * Git branch url.
         */
        val url: String,

        /**
         * Course plagiarism reports.
         */
        val plagiarismReports: List<PlagiarismReport>,

        /**
         * Task solutions.
         */
        val solutions: List<Solution>
) : Identifiable, Named
