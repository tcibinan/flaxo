package org.flaxo.rest.service.environment

import org.flaxo.core.env.Environment

interface RepositoryEnvironmentService {
    fun produceEnvironment(
            languageName: String,
            testingLanguageName: String,
            testingFrameworkName: String
    ): Environment
}