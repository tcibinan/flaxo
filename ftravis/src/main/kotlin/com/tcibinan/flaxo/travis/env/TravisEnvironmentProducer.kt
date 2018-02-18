package com.tcibinan.flaxo.travis.env

import com.tcibinan.flaxo.core.env.EnvironmentTool
import com.tcibinan.flaxo.core.language.Language

interface TravisEnvironmentProducer : EnvironmentTool {

    class UnsupportedLanguage(language: Language)
        : Throwable("Unsupported language ${language.name()} to use with travis")

}
