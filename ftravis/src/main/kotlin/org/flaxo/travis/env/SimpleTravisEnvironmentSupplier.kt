package org.flaxo.travis.env

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.env.Environment
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.StringEnvironmentFile
import org.flaxo.travis.TravisException
import org.flaxo.travis.UnsupportedLanguageException

/**
 * Travis jvm environment supplier implementation class.
 *
 * Should be replaced with more generified one in the future.
 */
data class SimpleTravisEnvironmentSupplier(private val language: Language? = null,
                                           private val testingLanguage: Language? = null,
                                           private val testingFramework: Framework? = null,
                                           private val travisWebHookUrl: String
) : TravisEnvironmentSupplier {

    private val jvmLanguages = setOf(Language.Java, Language.Kotlin)

    override fun with(language: Language?,
                      testingLanguage: Language?,
                      testingFramework: Framework?
    ): EnvironmentSupplier =
            withLanguage(language)
                    .withTestingLanguage(testingLanguage)
                    .withTestingFramework(testingFramework)

    private fun withLanguage(language: Language?): SimpleTravisEnvironmentSupplier =
            language
                    ?.let {
                        if (language in jvmLanguages) copy(language = language)
                        else throw UnsupportedLanguageException(language)
                    }
                    ?: this

    private fun withTestingLanguage(testingLanguage: Language?): SimpleTravisEnvironmentSupplier =
            testingLanguage
                    ?.let {
                        if (testingLanguage in jvmLanguages) copy(testingLanguage = testingLanguage)
                        else throw UnsupportedLanguageException(testingLanguage)
                    }
                    ?: this

    // currently where is no validations for testing frameworks
    private fun withTestingFramework(testingFramework: Framework?): SimpleTravisEnvironmentSupplier =
            testingFramework
                    ?.let {
                        copy(testingFramework = testingFramework)
                    }
                    ?: this

    override fun environment(): Environment {
        language ?: throw TravisException("There is no language for travis environment")
        testingLanguage ?: throw TravisException("There is no testing language for travis environment")
        testingFramework ?: throw TravisException("There is no testing framework for travis environment")

        return SimpleEnvironment(setOf(travisYmlFile()))
    }

    private fun travisYmlFile(): EnvironmentFile =
            StringEnvironmentFile(".travis.yml",
                    """
                        language: java
                        jdk:
                          - oraclejdk8
                        before_install:
                          - chmod +x gradlew

                        # disabling email notifications
                        notifications:
                          email: false
                          webhooks: $travisWebHookUrl
                    """.trimIndent()
            )
}