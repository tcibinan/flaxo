package com.tcibinan.flaxo.travis.env

import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.language.Language

/**
 * Travis environment supplier marker interface.
 */
interface TravisEnvironmentSupplier : EnvironmentSupplier {

    /**
     * Travis environment supplier unsupported language exception.
     */
    class UnsupportedLanguage(language: Language)
        : Throwable("Unsupported language ${language.name()} to use with travis")

}
