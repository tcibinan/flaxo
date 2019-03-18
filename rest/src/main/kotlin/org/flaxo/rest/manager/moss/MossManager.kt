package org.flaxo.rest.manager.moss

import org.flaxo.model.data.Task

/**
 * Moss plagiarism analysis manager.
 */
interface MossManager {

    /**
     * Analyses plagiarism of the given [task].
     *
     * @return Task with an updated plagiarism reports list.
     */
    fun analyse(task: Task): Task
}
