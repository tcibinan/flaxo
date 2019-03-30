package org.flaxo.rest.manager.environment

import org.amshove.kluent.shouldThrow
import org.flaxo.common.Language
import org.flaxo.gradle.GradleBuildTool
import org.flaxo.rest.manager.IncompatibleLanguageException
import org.flaxo.rest.manager.IncompatibleTestingFrameworkException
import org.flaxo.travis.env.SimpleTravisEnvironmentSupplier
import org.flaxo.travis.env.TravisEnvironmentSupplier
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object RepositoryEnvironmentServiceSpec : SubjectSpek<EnvironmentManager>({
    val firstLanguage = "java"
    val secondLanguage = "kotlin"
    val firstTestingFramework = "junit"
    val secondTestingFramework = "spek"

    val travisEnvironmentSupplier: TravisEnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(travisWebHookUrl = "travisWebHookUrl")
    val defaultBuildTools: Map<Language, GradleBuildTool> = mapOf(
            Language.Java to GradleBuildTool(travisEnvironmentSupplier),
            Language.Kotlin to GradleBuildTool(travisEnvironmentSupplier)
    )

    subject { SimpleEnvironmentManager(defaultBuildTools) }

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