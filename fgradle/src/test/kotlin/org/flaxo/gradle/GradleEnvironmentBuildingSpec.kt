package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.cmd.CmdExecutor
import org.flaxo.core.env.Environment
import org.flaxo.core.env.EnvironmentSupplier
import org.flaxo.core.env.SimpleEnvironment
import org.flaxo.core.framework.TestingFramework
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.KotlinLang
import org.flaxo.core.language.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.Data3
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on
import java.io.File

object GradleEnvironmentBuildingSpec : Spek({

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
        on { withLanguage(any()) }.thenReturn(it)
        on { withTestingLanguage(any()) }.thenReturn(it)
        on { withTestingFramework(any()) }.thenReturn(it)
        on { getEnvironment() }.thenReturn(SimpleEnvironment(emptySet()))
    }
    val environmentSupplier: (Language, Language, TestingFramework) -> Environment =
            { language, testingLanguage, testingFramework ->
                GradleBuildTool(travis)
                        .with(language, testingLanguage, testingFramework)
                        .getEnvironment()
            }

    describe("gradle environment") {
        on("building supplied environment for %s, %s, %s", *instrumentsCombinations)
        { language: Language,
          testingLanguage: Language,
          framework: TestingFramework,
          _: Unit ->

            val environment = environmentSupplier(language, testingLanguage, framework)

            val buildFile = environment.getFile(gradleBuildFile)
                    ?: throw EnvironmentFileNotFound("$gradleBuildFile wasn't found in the environment")

            val settingsFile = environment.getFile(gradleSettingsFile)
                    ?: throw EnvironmentFileNotFound("$gradleSettingsFile wasn't found in the environment")

            it("should create buildable project") {
                val tempDir = createTempDir("$language.$testingLanguage.$framework")
                tempDir.deleteOnExit()

                CmdExecutor.within(tempDir).execute("touch", gradleBuildFile)
                File(tempDir, gradleBuildFile).fillWith(buildFile.content)
                CmdExecutor.within(tempDir).execute("touch", gradleSettingsFile)
                File(tempDir, gradleSettingsFile).fillWith(settingsFile.content)
                GradleCmdExecutor.within(tempDir).build()
            }
        }
    }
})