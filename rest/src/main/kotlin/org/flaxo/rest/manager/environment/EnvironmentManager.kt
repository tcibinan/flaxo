package org.flaxo.rest.manager.environment

import org.flaxo.common.env.Environment

/**
 * Environment manager.
 */
interface EnvironmentManager {

    /**
     * Generates an environment based on the given [language], [testingLanguage] and [testingFramework].
     */
    fun produceEnvironment(language: String?, testingLanguage: String?, testingFramework: String?): Environment
}