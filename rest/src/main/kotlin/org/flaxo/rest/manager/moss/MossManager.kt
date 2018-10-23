package org.flaxo.rest.manager.moss

import org.flaxo.model.data.Course

/**
 * Moss manager.
 */
interface MossManager {

    /**
     * Analyses plagiarism of the given [course].
     *
     * @return Course with an updated plagiarism reports list.
     */
    fun analysePlagiarism(course: Course): Course
}
