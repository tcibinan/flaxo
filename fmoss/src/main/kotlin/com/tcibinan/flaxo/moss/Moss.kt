package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface Moss {
    val userId: String
    val language: String
    fun base(bases: List<EnvironmentFile>): Moss
    fun solutions(solutions: List<EnvironmentFile>): Moss
    fun analyse(): MossResult
}

