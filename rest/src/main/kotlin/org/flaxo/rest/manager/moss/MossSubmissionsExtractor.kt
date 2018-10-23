package org.flaxo.rest.manager.moss

import org.flaxo.model.data.Course
import org.flaxo.moss.MossSubmission

/**
 * Moss submissions extractor.
 */
interface MossSubmissionsExtractor {

    /**
     * Extract moss submissions using owner and student's repositories according to the given [course].
     *
     * Filters files by the valid extensions of the [Course.language].
     */
    fun extract(course: Course): List<MossSubmission>
}
