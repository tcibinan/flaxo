package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.DateTime

/**
 * Course task.
 *
 * Each task is associated with a git branch.
 */
@Serializable
data class Task(

        override val id: Long,

        override val name: String,

        /**
         * Associated git branch name.
         */
        val branch: String,

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
