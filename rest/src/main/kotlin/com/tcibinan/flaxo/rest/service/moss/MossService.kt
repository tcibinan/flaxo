package com.tcibinan.flaxo.rest.service.moss

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.moss.Moss

interface MossService {
    fun client(language: String): Moss
    fun extractMossTasks(course: Course): List<MossTask>
}

