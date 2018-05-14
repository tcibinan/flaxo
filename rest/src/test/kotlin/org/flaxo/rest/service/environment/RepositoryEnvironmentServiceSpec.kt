package org.flaxo.rest.service.environment

import org.amshove.kluent.shouldThrow
import org.flaxo.core.build.BuildTool
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.KotlinLang
import org.flaxo.core.language.Language
import org.flaxo.gradle.GradleBuildTool
import org.flaxo.rest.Application
import org.flaxo.rest.CodacyConfiguration
import org.flaxo.rest.CoreConfiguration
import org.flaxo.rest.DataConfiguration
import org.flaxo.rest.TravisConfiguration
import org.flaxo.rest.service.IncompatibleLanguageException
import org.flaxo.rest.service.IncompatibleTestingFrameworkException
import org.flaxo.travis.env.SimpleTravisEnvironmentSupplier
import org.flaxo.travis.env.TravisEnvironmentSupplier
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object RepositoryEnvironmentServiceSpec : SubjectSpek<RepositoryEnvironmentService>({
    val firstLanguage = "java"
    val secondLanguage = "kotlin"
    val firstTestingFramework = "junit"
    val secondTestingFramework = "spek"

    val languages: Map<String, Language> = mapOf(
            "java" to JavaLang,
            "kotlin" to KotlinLang
    )
    val testingFrameworks: Map<String, TestingFramework> = mapOf(
            "junit" to JUnitTestingFramework,
            "spek" to SpekTestingFramework
    )
    val travisEnvironmentSupplier: TravisEnvironmentSupplier =
            SimpleTravisEnvironmentSupplier(travisWebHookUrl = "travisWebHookUrl")
    val defaultBuildTools: Map<Language, BuildTool> = mapOf(
            JavaLang to GradleBuildTool(travisEnvironmentSupplier),
            KotlinLang to GradleBuildTool(travisEnvironmentSupplier)
    )

    subject { SimpleRepositoryEnvironmentService(languages, testingFrameworks, defaultBuildTools) }

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