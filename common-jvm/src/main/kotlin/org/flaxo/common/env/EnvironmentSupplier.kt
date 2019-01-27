package org.flaxo.common.env

import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.Language

/**
 * Environment supplier.
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
