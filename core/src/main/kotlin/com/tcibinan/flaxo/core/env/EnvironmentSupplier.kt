package com.tcibinan.flaxo.core.env

import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

/**
 * Environment supplier interface.
 */
interface EnvironmentSupplier {

    /**
     * @return the formed environment.
     */
    fun getEnvironment(): Environment

    /**
     * Returns the environment supplier with the given parameters.
     *
     * @param language of the creating environment.
     * @param testingLanguage of the creating environment.
     * @param testingFramework of the creating environment.
     * @return environment supplier with the given parameters.
     */
    fun with(language: Language,
             testingLanguage: Language,
             testingFramework: TestingFramework
    ) : EnvironmentSupplier
}