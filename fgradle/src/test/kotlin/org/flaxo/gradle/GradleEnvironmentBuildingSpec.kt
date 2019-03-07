package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.common.cmd.CmdExecutor
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.env.SimpleEnvironment
import org.flaxo.common.framework.TestingFramework
import org.flaxo.common.lang.JavaLang
import org.flaxo.common.lang.KotlinLang
import org.flaxo.common.lang.Language
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.Data3
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Files
import java.nio.file.Path

object GradleEnvironmentBuildingSpec : SubjectSpek<EnvironmentSupplier>({

    val gradleBuildFile = "build.gradle"
    val gradleSettingsFile = "settings.gradle"
    val supportedLanguages: Set<Language> = setOf(JavaLang, KotlinLang)

    val instrumentsCombinations: Array<Data3<Language, Language, TestingFramework, Unit>> =
            supportedLanguages
                    .flatMap { language ->
                        language.compatibleTestingLanguages.map { testingLanguage ->
                            language to testingLanguage
                        }
                    }
                    .flatMap { (language, testingLanguage) ->
                        testingLanguage.compatibleTestingFrameworks.map { testingFramework ->
                            Triple(language, testingLanguage, testingFramework)
                        }
                    }
                    .map { (language, testingLanguage, testingFramework) ->
                        data(language, testingLanguage, testingFramework, expected = Unit)
                    }
                    .toTypedArray()
    val travis: EnvironmentSupplier = mock {
        on { with(any(), any(), any()) }.thenReturn(it)
        on { environment() }.thenReturn(SimpleEnvironment(emptySet()))
    }

    subject { GradleBuildTool(travis) }

    describe("gradle environment") {
        on("building supplied environment for %s, %s, %s", *instrumentsCombinations)
        { language: Language,
          testingLanguage: Language,
          framework: TestingFramework,
          _: Unit ->

            val environment = subject.with(
                    language = language,
                    testingLanguage = testingLanguage,
                    testingFramework = framework
            ).environment()

            val buildFile = environment.file(gradleBuildFile)
                    ?: throw GradleException("$gradleBuildFile wasn't found in the environment")

            val settingsFile = environment.file(gradleSettingsFile)
                    ?: throw GradleException("$gradleSettingsFile wasn't found in the environment")

            it("should create buildable project") {
                val tempDir: Path = Files.createTempDirectory("$language.$testingLanguage.$framework")

                CmdExecutor.within(tempDir).execute("touch", gradleBuildFile)
                tempDir.resolve(gradleBuildFile).fillWith(buildFile.content)
                CmdExecutor.within(tempDir).execute("touch", gradleSettingsFile)
                tempDir.resolve(gradleSettingsFile).fillWith(settingsFile.content)
                GradleCmdExecutor.within(tempDir).build()
            }
        }
    }
})
