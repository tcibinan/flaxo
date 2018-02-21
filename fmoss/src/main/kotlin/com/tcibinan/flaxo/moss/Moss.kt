package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile

interface Moss {
    val userId: String
    val language: String
    fun base(bases: Set<EnvironmentFile>): Moss
    fun solutions(solutions: Set<EnvironmentFile>): Moss
    fun analyse(): MossResult
}

