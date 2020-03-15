package org.flaxo.rest.manager.plag

import org.flaxo.model.data.Task
import org.flaxo.moss.MossResult

/**
 * Common interface for plagiarism analyzers.
 */
interface PlagiarismAnalyser {

    /**
     * Analyze plagiarism of the [task].
     */
    fun analyse(task: Task): MossResult

}