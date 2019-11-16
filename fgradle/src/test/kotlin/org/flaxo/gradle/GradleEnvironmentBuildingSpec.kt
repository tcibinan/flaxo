package org.flaxo.gradle

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.flaxo.common.cmd.CmdExecutor
import org.flaxo.common.deleteDirectoryRecursively
import org.flaxo.common.env.EnvironmentSupplier
import org.flaxo.common.env.SimpleEnvironment
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.Data3
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Files
import java.nio.file.Path

object GradleEnvironmentBuildingSpec : SubjectSpek<EnvironmentSupplier>({

    val supportedLanguages: Set<Language> = setOf(Language.Java, Language.Kotlin)

    val instrumentsCombinations: Array<Data3<Language, Language, Framework, Unit>> =
            supportedLanguages
                    .flatMap { language ->
                        language.testingLanguages.map { testingLanguage ->
                            language to testingLanguage
                        }
                    }
                    .flatMap { (language, testingLanguage) ->
                        testingLanguage.testingFrameworks.map { testingFramework ->
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
          framework: Framework,
          _: Unit ->

            val environment = subject.with(
                    language = language,
                    testingLanguage = testingLanguage,
                    testingFramework = framework
            ).environment()

            it("should create buildable project") {
                val tempDir: Path = Files.createTempDirectory("$language.$testingLanguage.$framework")
                try {
                    environment.files().forEach { file -> file.toLocalFile(tempDir).flush() }
                    with(CmdExecutor.within(tempDir)) {
                        execute("chmod", "+x", "gradlew")
                        execute("./gradlew", "build")
                    }
                } finally {
                    deleteDirectoryRecursively(tempDir)
                }
            }
        }
    }
})
