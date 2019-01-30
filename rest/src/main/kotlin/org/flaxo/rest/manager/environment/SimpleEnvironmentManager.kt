package org.flaxo.rest.manager.environment

import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.Language
import org.flaxo.rest.manager.IncompatibleLanguageException
import org.flaxo.rest.manager.IncompatibleTestingFrameworkException
import org.flaxo.rest.manager.NoDefaultBuildToolException

/**
 * Environment manager implementation.
 */
class SimpleEnvironmentManager(
        private val languages: List<Language>,
        private val testingFrameworks: List<TestingFramework>,
        private val defaultBuildTools: Map<Language, EnvironmentSupplier>
) : EnvironmentManager {

    override fun produceEnvironment(language: String?,
                                    testingLanguage: String?,
                                    testingFramework: String?
    ): Environment = produceEnvironment(
            language = languages.find { it.name == language },
            testingLanguage = languages.find { it.name == testingLanguage },
            testingFramework = testingFrameworks.find { it.name == testingFramework }
    )

    private fun produceEnvironment(language: Language?,
                                   testingLanguage: Language?,
                                   testingFramework: TestingFramework?
    ): Environment {
        if (language == null
                || testingLanguage == null
                || testingFramework == null) {
            return Environment.empty()
        }

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