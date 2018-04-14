package org.flaxo.rest.service.travis

import org.flaxo.rest.TestApplication
import org.flaxo.rest.TravisConfiguration
import org.flaxo.rest.service.AbsentEnvironmentPropertyException
import org.flaxo.travis.TravisException
import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object TravisServiceSpec : SubjectSpek<TravisService>({

    val context = AnnotationConfigApplicationContext(
            TestApplication::class.java,
            TravisConfiguration::class.java
    )
    val githubUsername = context.environment.getProperty("GITHUB_TEST_NAME")
            ?: throw AbsentEnvironmentPropertyException("GITHUB_TEST_NAME")
    val githubToken = context.environment.getProperty("GITHUB_TEST_TOKEN")
            ?: throw AbsentEnvironmentPropertyException("GITHUB_TEST_TOKEN")
    val githubRepositoryName = context.environment.getProperty("GITHUB_REPOSITORY_ID")
            ?: throw AbsentEnvironmentPropertyException("GITHUB_REPOSITORY_ID")
    val travisToken = context.environment.getProperty("TRAVIS_TEST_TOKEN")
            ?: throw AbsentEnvironmentPropertyException("TRAVIS_TEST_TOKEN")

    subject { context.getBean("travisService", TravisService::class.java) }

    describe("travis service") {
        on("getting travis token") {
            val generatedTravisToken = subject
                    .retrieveTravisToken(githubUsername, githubToken)

            val travis = subject.travis(generatedTravisToken)

            it("should return valid token") {
                val user = travis.getUser()
                        .getOrElseThrow { errorBody ->
                            TravisException("Travis user wasn't received due to: ${errorBody.string()}")
                        }

                user.login shouldBe githubUsername
            }
        }
    }

    describe("travis client") {
        val travis = subject.travis(travisToken)

        on("deactivating a repository") {
            val repository = travis.deactivate(githubUsername, githubRepositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis repository wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to inactive status") {
                repository.active shouldBe false
            }
        }

        on("activating a repository") {
            val repository = travis.activate(githubUsername, githubRepositoryName)
                    .getOrElseThrow { errorBody ->
                        TravisException("Travis user wasn't received due to: ${errorBody.string()}")
                    }

            it("should set repository to active status") {
                repository.active shouldBe true
            }
        }
    }
})