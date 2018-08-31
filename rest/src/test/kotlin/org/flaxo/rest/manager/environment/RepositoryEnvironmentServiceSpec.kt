package org.flaxo.rest.manager.environment

import org.amshove.kluent.shouldThrow
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.lang.JavaLang
import org.flaxo.core.lang.KotlinLang
import org.flaxo.core.lang.Language
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
    val defaultBuildTools: Map<Language, GradleBuildTool> = mapOf(
            JavaLang to GradleBuildTool(travisEnvironmentSupplier),
            KotlinLang to GradleBuildTool(travisEnvironmentSupplier)
    )

    subject { SimpleEnvironmentManager(languages, testingFrameworks, defaultBuildTools) }

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