package org.flaxo.rest.manager.moss

import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.MossResult

/**
 * Moss manager.
 */
interface MossManager {

    /**
     * Returns a moss client for the given programming [language].
     */
    fun client(language: String): Moss

    /**
     * Extract moss analysis submissions from the given [course].
     */
    fun extractSubmissions(course: Course): List<MossSubmission>

    /**
     * Retrieves analysis result by the given [mossResultUrl].
     */
    fun retrieveAnalysisResult(mossResultUrl: String): MossResult
}

