package com.tcibinan.flaxo.travis.env

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.EnvironmentSupplier
import com.tcibinan.flaxo.core.env.SimpleEnvironment
import com.tcibinan.flaxo.core.env.SimpleEnvironmentFile
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.KotlinLang
import com.tcibinan.flaxo.core.language.Language

class SimpleTravisEnvironmentSupplier(private val language: Language,
                                      private val testingLanguage: Language = language,
                                      private val framework: TestingFramework,
                                      private val travisWebHookUrl: String
) : TravisEnvironmentSupplier {

    private val jvmLanguages = setOf(JavaLang, KotlinLang)

    override fun with(language: Language,
                      testingLanguage: Language,
                      testingFramework: TestingFramework
    ): EnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(language, testingLanguage, testingFramework, travisWebHookUrl)

    override fun getEnvironment(): Environment {
        if (language in jvmLanguages
                && testingLanguage in jvmLanguages) {
            return SimpleEnvironment(setOf(
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
            ))
        } else {
            if (language !in jvmLanguages) {
                throw TravisEnvironmentSupplier.UnsupportedLanguage(language)
            }
            if (testingLanguage !in jvmLanguages) {
                throw TravisEnvironmentSupplier.UnsupportedLanguage(testingLanguage)
            }
            throw RuntimeException("Travis environment can't be created with " +
                    "such an environment: $language:$testingLanguage:$framework")
        }
    }
}