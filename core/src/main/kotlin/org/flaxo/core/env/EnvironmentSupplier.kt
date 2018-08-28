package org.flaxo.core.env

import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language

/**
 * Environment supplier interface.
 */
interface EnvironmentSupplier {

    /**
     * @return Generated environment.
     */
    fun environment(): Environment

    /**
     * Returns the environment supplier with the given parameters.
     *
     * @param language of the creating environment.
     * @param testingLanguage of the creating environment.
     * @param testingFramework of the creating environment.
     * @return Environment supplier with the given parameters.
     */
    fun with(language: Language? = null,
             testingLanguage: Language? = null,
             testingFramework: TestingFramework? = null
    ): EnvironmentSupplier
}