package com.tcibinan.flaxo.travis.env

import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.language.Language

interface TravisEnvironmentSupplier : EnvironmentSupplier {

    class UnsupportedLanguage(language: Language)
        : Throwable("Unsupported language ${language.name()} to use with travis")

}
