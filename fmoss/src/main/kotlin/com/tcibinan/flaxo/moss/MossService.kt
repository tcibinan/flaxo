package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.language.Language

interface MossService {
    fun client(language: Language): Moss
}

