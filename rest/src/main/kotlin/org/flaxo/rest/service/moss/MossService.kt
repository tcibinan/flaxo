package org.flaxo.rest.service.moss

import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.MossResult

interface MossService {
    fun client(language: String): Moss
    fun extractMossTasks(course: Course): List<MossTask>
    fun retrieveAnalysisResult(mossResultUrl: String): MossResult
}

