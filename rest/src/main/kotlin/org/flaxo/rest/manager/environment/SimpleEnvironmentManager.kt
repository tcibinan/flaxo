package org.flaxo.rest.manager.environment

import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.lang.Language
import org.flaxo.rest.manager.IncompatibleLanguageException
import org.flaxo.rest.manager.IncompatibleTestingFrameworkException
import org.flaxo.rest.manager.NoDefaultBuildToolException
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.UnsupportedTestingFrameworkException

/**
 * Environment manager implementation.
 */
class SimpleEnvironmentManager(
        private val languages: Map<String, Language>,
        private val testingFrameworks: Map<String, TestingFramework>,
        private val defaultBuildTools: Map<Language, EnvironmentSupplier>
) : EnvironmentManager {

    override fun produceEnvironment(language: String,
                                    testingLanguage: String,
                                    testingFramework: String
    ): Environment = produceEnvironment(
            language = languages[language]
                    ?: throw UnsupportedLanguageException(language),
            testingLanguage = languages[testingLanguage]
                    ?: throw UnsupportedLanguageException(testingLanguage),
            testingFramework = testingFrameworks[testingFramework]
                    ?: throw UnsupportedTestingFrameworkException(testingFramework)
    )

    private fun produceEnvironment(language: Language,
                                   testingLanguage: Language,
                                   testingFramework: TestingFramework
    ): Environment {
        testingLanguage shouldSuit language
        testingFramework shouldSuit testingLanguage

        val buildTool = defaultBuildTools[language]
                ?: throw NoDefaultBuildToolException(language)

        return buildTool
                .with(language, testingLanguage, testingFramework)
                .environment()
    }

    private infix fun TestingFramework.shouldSuit(testingLanguage: Language) {
        testingLanguage.takeIf { it worksWith this }
                ?: throw IncompatibleTestingFrameworkException(this, testingLanguage)
    }

    private infix fun Language.shouldSuit(language: Language) {
        language.takeIf { it canBeTestedBy this }
                ?: throw IncompatibleLanguageException(language, this)
    }

}