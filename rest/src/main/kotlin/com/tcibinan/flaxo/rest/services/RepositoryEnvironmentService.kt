package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.core.Environment

interface RepositoryEnvironmentService {
    fun produceEnvironment(
            languageName: String,
            testingLanguageName: String,
            testingFrameworkName: String
    ): Environment
}