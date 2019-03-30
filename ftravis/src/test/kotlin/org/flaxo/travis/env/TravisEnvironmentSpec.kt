package org.flaxo.travis.env

import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertTrue

object TravisEnvironmentSpec : SubjectSpek<TravisEnvironmentSupplier>({

    val travisWebHookUrl = "http://example.com/travis/web/hook"

    subject {
        SimpleTravisEnvironmentSupplier(Language.Java, Language.Java, Framework.JUnit, travisWebHookUrl)
    }

    describe("travis environment") {

        on("creating environment") {
            val environment = subject.environment()
            val travisYml = environment.file(".travis.yml")

            it("should contain .travis.yml") {
                assertTrue { travisYml != null }
            }

            it("should contain non blank .travis.yml") {
                assertTrue {
                    travisYml
                            ?.content
                            ?.isNotBlank()
                            ?: throw EnvironmentFileNotFound("$travisYml file not found in the environment")
                }
            }

            it("should contain .travis.yml with webhookurl") {
                assertTrue {
                    travisYml
                            ?.content
                            ?.contains(travisWebHookUrl)
                            ?: throw EnvironmentFileNotFound("$travisYml file not found in the environment")
                }
            }
        }
    }

})

class EnvironmentFileNotFound(message: String) : RuntimeException(message)
