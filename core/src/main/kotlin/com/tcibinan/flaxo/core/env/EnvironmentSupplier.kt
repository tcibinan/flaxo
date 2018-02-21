package com.tcibinan.flaxo.core.env

import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

interface EnvironmentSupplier {
    fun getEnvironment(): Environment
    fun with(language: Language,
             testingLanguage: Language,
             testingFramework: TestingFramework
    ) : EnvironmentSupplier
}