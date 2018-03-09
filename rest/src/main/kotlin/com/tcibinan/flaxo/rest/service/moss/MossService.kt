package com.tcibinan.flaxo.rest.service.moss

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.moss.Moss
import com.tcibinan.flaxo.moss.MossResult

interface MossService {
    fun client(language: String): Moss
    fun extractMossTasks(course: Course): List<MossTask>
    fun retrieveAnalysisResult(mossResultUrl: String): MossResult
}

