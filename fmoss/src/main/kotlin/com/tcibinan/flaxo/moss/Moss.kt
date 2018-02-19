package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.language.Language

interface Moss {
    val userId: String
    val language: Language
    fun base(bases: Set<EnvironmentFile>): Moss
    fun solutions(solutions: Set<EnvironmentFile>): Moss
    fun analyse(): MossResult
}

