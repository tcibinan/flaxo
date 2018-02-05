package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.core.Environment

interface RepositoryEnvironmentService {
    fun produceEnvironment(
            languageName: String,
            testingLanguageName: String,
            testingFrameworkName: String
    ): Environment
}