package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.CoreConfiguration
import com.tcibinan.flaxo.rest.service.IncompatibleLanguage
import com.tcibinan.flaxo.rest.service.IncompatibleTestingFramework
import com.tcibinan.flaxo.rest.service.git.RepositoryEnvironmentService
import io.kotlintest.matchers.shouldThrow
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object RepositoryEnvironmentServiceSpec: SubjectSpek<RepositoryEnvironmentService>({
    val firstLanguage = "java"
    val secondLanguage = "kotlin"
    val firstTestingFramework = "junit"
    val secondTestingFramework = "spek"

    val context = AnnotationConfigApplicationContext(CoreConfiguration::class.java)

    subject { context.getBean("repositoryEnvironmentService", RepositoryEnvironmentService::class.java) }

    describe("repository environment service") {

        on("creating an environment with incompatible languages") {
            it("should throw a concrete exception") {
                shouldThrow<IncompatibleLanguage> {
                    subject.produceEnvironment(secondLanguage, firstLanguage, firstTestingFramework)
                }
            }
        }

        on("creating an environment with incompatible testing language and testing framework") {
            it("should throw a concrete exception") {
                shouldThrow<IncompatibleTestingFramework> {
                    subject.produceEnvironment(firstLanguage, firstLanguage, secondTestingFramework)
                }
            }
        }

        on("creating an environment with compatible languages and framework") {
            it("should successfully create an environment") {
                subject.produceEnvironment(firstLanguage, secondLanguage, secondTestingFramework)
            }
        }
    }

})