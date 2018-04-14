package com.tcibinan.flaxo.travis.env

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.env.SimpleEnvironment
import com.tcibinan.flaxo.core.env.SimpleEnvironmentFile
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.KotlinLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.travis.TravisException
import com.tcibinan.flaxo.travis.UnsupportedLanguageException

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
            if (language in jvmLanguages) {
                copy(language = language)
            } else {
                throw UnsupportedLanguageException(language)
            }

    override fun withTestingLanguage(testingLanguage: Language): EnvironmentSupplier =
            if (testingLanguage in jvmLanguages) {
                copy(testingLanguage = testingLanguage)
            } else {
                throw UnsupportedLanguageException(testingLanguage)
            }

    // currently where is no validations for testing frameworks
    override fun withTestingFramework(testingFramework: TestingFramework): EnvironmentSupplier =
            copy(testingFramework = testingFramework)

    override fun getEnvironment(): Environment {
        language
                ?: throw TravisException("There is no language for travis environment")
        testingLanguage
                ?: throw TravisException("There is no testing language for travis environment")
        testingFramework
                ?: throw TravisException("There is no testing framework for travis environment")

        return SimpleEnvironment(setOf(produceTravisYmlFile()))
    }

    private fun produceTravisYmlFile(): EnvironmentFile =
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