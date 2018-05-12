package org.flaxo.rest.service.environment

import org.flaxo.core.env.Environment

/**
 * Repository environment producer service.
 */
interface RepositoryEnvironmentService {

    /**
     * Returns an environment based on the given [languageName], [testingLanguageName] and [testingFrameworkName].
     */
    fun produceEnvironment(languageName: String,
                           testingLanguageName: String,
                           testingFrameworkName: String
    ): Environment
}