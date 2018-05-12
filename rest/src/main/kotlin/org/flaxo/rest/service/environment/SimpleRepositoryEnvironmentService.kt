package org.flaxo.rest.service.environment

import org.flaxo.core.build.BuildTool
import org.flaxo.core.env.Environment
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.Language
import org.flaxo.rest.service.IncompatibleLanguageException
import org.flaxo.rest.service.IncompatibleTestingFrameworkException
import org.flaxo.rest.service.NoDefaultBuildTool
import org.flaxo.rest.service.UnsupportedLanguage
import org.flaxo.rest.service.UnsupportedTestingFramework

/**
 * Repository environment producing service implementation.
 */
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

        testingLanguage shouldSuit language
        testingFramework shouldSuit testingLanguage

        val buildTool = defaultBuildTools[language]
                ?: throw NoDefaultBuildTool(language)

        return buildTool
                .with(language, testingLanguage, testingFramework)
                .getEnvironment()
    }

    private infix fun TestingFramework.shouldSuit(testingLanguage: Language) {
        testingLanguage.takeIf { it.worksWith(this) }
                ?: throw IncompatibleTestingFrameworkException(this, testingLanguage)
    }

    private infix fun Language.shouldSuit(language: Language) {
        language.takeIf { it.canBeTestedBy(this) }
                ?: throw IncompatibleLanguageException(language, this)
    }

}