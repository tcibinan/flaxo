package com.tcibinan.flaxo.rest.service.environment

import com.tcibinan.flaxo.core.env.Environment

interface RepositoryEnvironmentService {
    fun produceEnvironment(
            languageName: String,
            testingLanguageName: String,
            testingFrameworkName: String
    ): Environment
}