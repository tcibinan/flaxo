package org.flaxo.rest.manager.environment

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.rest.manager.IncompatibleLanguageException
import org.flaxo.rest.manager.IncompatibleTestingFrameworkException
import org.flaxo.rest.manager.NoDefaultBuildToolException

/**
 * Environment manager implementation.
 */
class SimpleEnvironmentManager(
        private val defaultBuildTools: Map<Language, EnvironmentSupplier>
) : EnvironmentManager {

    override fun produceEnvironment(language: String?,
                                    testingLanguage: String?,
                                    testingFramework: String?
    ): Environment = produceEnvironment(
            language = language?.let { Language.from(it) },
            testingLanguage = testingLanguage?.let { Language.from(it) },
            testingFramework = testingFramework?.let { Framework.from(it) }
    )

    private fun produceEnvironment(language: Language?,
                                   testingLanguage: Language?,
                                   testingFramework: Framework?
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

    private infix fun Framework.shouldSuit(testingLanguage: Language) {
        testingLanguage.takeIf { it worksWith this }
                ?: throw IncompatibleTestingFrameworkException(this, testingLanguage)
    }

    private infix fun Language.shouldSuit(language: Language) {
        language.takeIf { it canBeTestedBy this }
                ?: throw IncompatibleLanguageException(language, this)
    }

}