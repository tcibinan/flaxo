package com.tcibinan.flaxo.travis

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.SimpleEnvironment
import com.tcibinan.flaxo.core.env.SimpleFile
import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.KotlinLang
import com.tcibinan.flaxo.core.language.Language

class SimpleTravisEnvironmentTool(private val language: Language,
                                  private val testingLanguage: Language = language,
                                  private val framework: TestingFramework
) : TravisEnvironmentProducer {

    private val jvmLanguages = setOf(JavaLang, KotlinLang)

    override fun produceEnvironment(): Environment {
        if (language in jvmLanguages
                && testingLanguage in jvmLanguages) {
            return SimpleEnvironment(setOf(
                    SimpleFile(".travis.yml",
                            """
                                language: java
                                jdk:
                                  - oraclejdk8

                                # disabling email notifications
                                notifications:
                                  email: false
                            """.trimIndent()
                    )
            ))
        } else {
            if (language !in jvmLanguages) {
                throw TravisEnvironmentProducer.UnsupportedLanguage(language)
            }
            if (testingLanguage !in jvmLanguages) {
                throw TravisEnvironmentProducer.UnsupportedLanguage(testingLanguage)
            }
            throw RuntimeException("Travis environment can't be created with " +
                    "such an environment: $language:$testingLanguage:$framework")
        }
    }
}