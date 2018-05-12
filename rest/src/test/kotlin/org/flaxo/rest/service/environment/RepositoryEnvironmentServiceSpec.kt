package org.flaxo.rest.service.environment

import org.amshove.kluent.shouldThrow
import org.flaxo.rest.CoreConfiguration
import org.flaxo.rest.service.IncompatibleLanguageException
import org.flaxo.rest.service.IncompatibleTestingFrameworkException
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
            it("should throw an IncompatibleLanguage exception") {
                {
                    subject.produceEnvironment(secondLanguage, firstLanguage, firstTestingFramework)
                } shouldThrow IncompatibleLanguageException::class
            }
        }

        on("creating an environment with incompatible testing language and testing framework") {
            it("should throw an IncompatibleTestingFramework exception") {
                {
                    subject.produceEnvironment(firstLanguage, firstLanguage, secondTestingFramework)
                } shouldThrow IncompatibleTestingFrameworkException::class
            }
        }

        on("creating an environment with compatible languages and framework") {
            it("should successfully create an environment") {
                subject.produceEnvironment(firstLanguage, firstLanguage, firstTestingFramework)
            }
        }
    }

})