package com.tcibinan.flaxo.rest.service.environment

import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.rest.service.IncompatibleLanguage
import com.tcibinan.flaxo.rest.service.IncompatibleTestingFramework
import com.tcibinan.flaxo.rest.service.NoDefaultBuildTool
import com.tcibinan.flaxo.rest.service.UnsupportedLanguage
import com.tcibinan.flaxo.rest.service.UnsupportedTestingFramework

class SimpleRepositoryEnvironmentService(
        private val languages: Map<String, Language>,
        private val testingFrameworks: Map<String, TestingFramework>,
        private val defaultBuildTools: Map<Language, BuildTool>
) : RepositoryEnvironmentService {

    override fun produceEnvironment(languageName: String,
                                    testingLanguageName: String,
                                    testingFrameworkName: String
    ): Environment {
        val language = languages[languageName]
                ?: throw UnsupportedLanguage(languageName)
        val testingLanguage = languages[testingLanguageName]
                ?: throw UnsupportedLanguage(testingLanguageName)
        val testingFramework = testingFrameworks[testingFrameworkName]
                ?: throw UnsupportedTestingFramework(testingFrameworkName)

        testingLanguage.shouldSuited(language)
        testingFramework.shouldSuited(testingLanguage)

        val buildTool = defaultBuildTools[language]
                ?: throw NoDefaultBuildTool(language)

        return buildTool
                .with(language, testingLanguage, testingFramework)
                .getEnvironment()
    }

    private fun TestingFramework.shouldSuited(testingLanguage: Language) {
        if (!testingLanguage.worksWith(this)) {
            throw IncompatibleTestingFramework(this, testingLanguage)
        }
    }

    private fun Language.shouldSuited(language: Language) {
        if (!language.canBeTestedBy(this)) {
            throw IncompatibleLanguage(language, this)
        }
    }

}