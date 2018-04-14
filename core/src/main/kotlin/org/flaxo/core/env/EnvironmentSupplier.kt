package org.flaxo.core.env

import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language

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
    fun with(language: Language? = null,
             testingLanguage: Language? = null,
             testingFramework: TestingFramework? = null
    ): EnvironmentSupplier = let { buildTool ->
        language?.let { buildTool.withLanguage(it) } ?: buildTool
    }.let { buildTool ->
        testingLanguage?.let { buildTool.withTestingLanguage(it) } ?: buildTool
    }.let { buildTool ->
        testingFramework?.let { buildTool.withTestingFramework(it) } ?: buildTool
    }


    /**
     * Returns the environment supplier with the given language.
     *
     * @param language of the creating environment.
     * @return environment supplier with the given language.
     */
    fun withLanguage(language: Language): EnvironmentSupplier


    /**
     * Returns the environment supplier with the given testing language.
     *
     * @param testingLanguage of the creating environment.
     * @return environment supplier with the given testing language.
     */
    fun withTestingLanguage(testingLanguage: Language): EnvironmentSupplier


    /**
     * Returns the environment supplier with the given testing framework.
     *
     * @param testingFramework of the creating environment.
     * @return environment supplier with the given testing framework.
     */
    fun withTestingFramework(testingFramework: TestingFramework): EnvironmentSupplier
}