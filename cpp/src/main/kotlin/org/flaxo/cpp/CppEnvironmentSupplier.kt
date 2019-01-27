package org.flaxo.cpp

import org.flaxo.common.env.Environment
import org.flaxo.common.lang.CppLang
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.framework.BashInputOutputTestingFramework
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.BashLang
import org.flaxo.common.lang.Language

/**
 * C++ environment supplier.
 */
class CppEnvironmentSupplier(private val language: Language,
                             private val testingLanguage: Language,
                             private val testingFramework: TestingFramework,
                             private val travisWebHookUrl: String
) : EnvironmentSupplier {

    init {
        if (!isCppAndBashIOEnvironment()) {
            throw CppEnvironmentException("Given group of technologies are not supported: " +
                    "($language, $testingLanguage, $testingFramework)")
        }
    }

    private fun isCppAndBashIOEnvironment(): Boolean =
            language == CppLang && testingLanguage == BashLang
                    && testingFramework == BashInputOutputTestingFramework

    override fun environment(): Environment =
            if (isCppAndBashIOEnvironment()) CppBashEnvironment(travisWebHookUrl)
            else throw CppEnvironmentException("Given group of technologies are not supported: " +
                    "($language, $testingLanguage, $testingFramework)")

    override fun with(language: Language?,
                      testingLanguage: Language?,
                      testingFramework: TestingFramework?
    ): EnvironmentSupplier =
            CppEnvironmentSupplier(
                    language = language ?: this.language,
                    testingLanguage = testingLanguage ?: this.testingLanguage,
                    testingFramework = testingFramework ?: this.testingFramework,
                    travisWebHookUrl = travisWebHookUrl
            )

}
