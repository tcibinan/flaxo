package org.flaxo.rest.manager.plag

import org.flaxo.common.data.PlagiarismReport
import org.flaxo.model.data.Task

/**
 * Plagiarism analysis manager.
 */
interface PlagiarismAnalysisManager {

    /**
     * Analyses plagiarism of the given [task].
     *
     * @return Plagiarism report of the given [task].
     */
    fun analyse(task: Task): PlagiarismReport
}
