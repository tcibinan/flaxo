package org.flaxo.rest.manager.plagiarism

import org.flaxo.model.data.Task
import org.flaxo.moss.MossResult

/**
 * Common interface for plagiarism analyzers.
 */
interface PlagiarismAnalyser {

    fun analyse(task: Task): MossResult

}