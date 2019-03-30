package org.flaxo.cpp

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier

/**
 * C++ environment supplier.
 */
class CppEnvironmentSupplier(private val language: Language,
                             private val testingLanguage: Language,
                             private val testingFramework: Framework,
                             private val travisWebHookUrl: String
) : EnvironmentSupplier {

    init {
        if (!isCppAndBashIOEnvironment()) {
            throw CppEnvironmentException("Given group of technologies are not supported: " +
                    "($language, $testingLanguage, $testingFramework)")
        }
    }

    private fun isCppAndBashIOEnvironment(): Boolean = language == Language.Cpp
            && testingLanguage == Language.Bash
            && testingFramework == Framework.BashIO

    override fun environment(): Environment =
            if (isCppAndBashIOEnvironment()) CppBashEnvironment(travisWebHookUrl)
            else throw CppEnvironmentException("Given group of technologies are not supported: " +
                    "($language, $testingLanguage, $testingFramework)")

    override fun with(language: Language?,
                      testingLanguage: Language?,
                      testingFramework: Framework?
    ): EnvironmentSupplier =
            CppEnvironmentSupplier(
                    language = language ?: this.language,
                    testingLanguage = testingLanguage ?: this.testingLanguage,
                    testingFramework = testingFramework ?: this.testingFramework,
                    travisWebHookUrl = travisWebHookUrl
            )
}
