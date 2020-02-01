package org.flaxo.rest.manager.moss

import org.flaxo.common.data.PlagiarismReport
import org.flaxo.model.data.Task

/**
 * Moss plagiarism analysis manager.
 */
interface MossManager {

    /**
     * Analyses plagiarism of the given [task].
     *
     * @return Plagiarism report of the given [task].
     */
    fun analyse(task: Task): PlagiarismReport
}
