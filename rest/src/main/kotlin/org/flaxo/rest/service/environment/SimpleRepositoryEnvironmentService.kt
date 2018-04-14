package org.flaxo.rest.service.environment

import org.flaxo.core.build.BuildTool
import org.flaxo.core.env.Environment
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language
import org.flaxo.rest.service.IncompatibleLanguage
import org.flaxo.rest.service.IncompatibleTestingFramework
import org.flaxo.rest.service.NoDefaultBuildTool
import org.flaxo.rest.service.UnsupportedLanguage
import org.flaxo.rest.service.UnsupportedTestingFramework

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