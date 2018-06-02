package org.flaxo.travis.env

import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.env.SimpleEnvironmentFile
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.KotlinLang
import org.flaxo.core.language.Language
import org.flaxo.travis.TravisException
import org.flaxo.travis.UnsupportedLanguageException

/**
 * Travis jvm environment supplier implementation class.
 *
 * Should be replaced with more generified one in the future.
 */
data class SimpleTravisEnvironmentSupplier(private val language: Language? = null,
                                           private val testingLanguage: Language? = null,
                                           private val testingFramework: TestingFramework? = null,
                                           private val travisWebHookUrl: String
) : TravisEnvironmentSupplier {

    private val jvmLanguages = setOf(JavaLang, KotlinLang)

    override fun withLanguage(language: Language): EnvironmentSupplier =
            if (language in jvmLanguages) copy(language = language)
            else throw UnsupportedLanguageException(language)

    override fun withTestingLanguage(testingLanguage: Language): EnvironmentSupplier =
            if (testingLanguage in jvmLanguages) copy(testingLanguage = testingLanguage)
            else throw UnsupportedLanguageException(testingLanguage)

    // currently where is no validations for testing frameworks
    override fun withTestingFramework(testingFramework: TestingFramework): EnvironmentSupplier =
            copy(testingFramework = testingFramework)

    override fun getEnvironment(): Environment {
        language ?: throw TravisException("There is no language for travis environment")
        testingLanguage ?: throw TravisException("There is no testing language for travis environment")
        testingFramework ?: throw TravisException("There is no testing framework for travis environment")

        return SimpleEnvironment(setOf(travisYmlFile()))
    }

    private fun travisYmlFile(): EnvironmentFile =
            SimpleEnvironmentFile(".travis.yml",
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