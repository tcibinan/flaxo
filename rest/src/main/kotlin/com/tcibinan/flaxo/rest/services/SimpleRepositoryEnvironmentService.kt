package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.core.Environment
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.rest.NoDefaultBuildTool
import com.tcibinan.flaxo.rest.IncompatibleLanguage
import com.tcibinan.flaxo.rest.IncompatibleTestingFramework
import com.tcibinan.flaxo.rest.UnsupportedLanguage
import com.tcibinan.flaxo.rest.UnsupportedTestingFramework

class SimpleRepositoryEnvironmentService(
        val languages: Map<String, Language>,
        val testingFrameworks: Map<String, TestingFramework>,
        val defaultBuildTools: Map<Language, () -> BuildTool>
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

        val buildToolProducer = defaultBuildTools[language]
                ?: throw NoDefaultBuildTool(language)

        val buildTool = buildToolProducer.invoke()

        return buildTool
                .withLanguage(language)
                .withTestingsLanguage(testingLanguage)
                .withTestingFramework(testingFramework)
                .buildEnvironment()
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