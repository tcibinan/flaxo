package org.flaxo.core.lang

import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.flaxo.core.framework.BashInputOutputTestingFramework
import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object AbstractLanguageSpec : Spek({
    val name = "language"
    val extensions = setOf("txt", "zip")
    val testingLanguages = setOf(JavaLang, KotlinLang)
    val testingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
    val incompatibleTestingLanguage = BashLang
    val incompatibleTestingFramework = BashInputOutputTestingFramework

    describe("language object") {
        val language = object : AbstractLanguage(
                name = name,
                extensions = extensions,
                testingLanguages = testingLanguages,
                testingFrameworks = testingFrameworks
        ) {}

        on("getting testing frameworks") {
            it("should return original list of testing frameworks") {
                language.compatibleTestingFrameworks shouldEqual testingFrameworks
            }
        }

        on("checking if any of the original testing frameworks works with the language") {
            it("should return true") {
                testingFrameworks.forEach { (language worksWith it).shouldBeTrue() }
            }
        }

        on("checking if an incompatible testing framework works with the language") {
            it("should return false") {
                (language worksWith incompatibleTestingFramework).shouldBeFalse()
            }
        }
    }

    describe("language object that doesn't have itself as a testing language") {
        val language = object : AbstractLanguage(
                name = name,
                extensions = extensions,
                testingLanguages = testingLanguages,
                testingFrameworks = testingFrameworks
        ) {}

        on("getting testing languages") {
            it("should return original list of testing languages") {
                language.compatibleTestingLanguages shouldEqual testingLanguages
            }
        }

        on("checking if any of the original testing languages can be used for testing the language") {
            it("should return true") {
                testingLanguages.forEach { (language canBeTestedBy it).shouldBeTrue() }
            }
        }

        on("checking if the language can be used for testing itself") {
            it("should return false") {
                (language canBeTestedBy language).shouldBeFalse()
            }
        }

        on("checking if an incompatible testing language can be used for testing the language") {
            it("should return false") {
                (language canBeTestedBy incompatibleTestingLanguage).shouldBeFalse()
            }
        }
    }

    describe("language object that has itself as a testing language") {
        val language = object : AbstractLanguage(
                name = name,
                extensions = extensions,
                testingLanguages = testingLanguages + Itself,
                testingFrameworks = testingFrameworks
        ) {}

        on("getting testing languages") {
            it("should return list of testing languages plus the language") {
                language.compatibleTestingLanguages shouldEqual (testingLanguages + language)
            }
        }

        on("checking if any of the original testing languages can be used for testing the language") {
            it("should return true") {
                testingLanguages.forEach { (language canBeTestedBy it).shouldBeTrue() }
            }
        }

        on("checking if the language can be used for testing itself") {
            it("should return true") {
                (language canBeTestedBy language).shouldBeTrue()
            }
        }

        on("checking if an incompatible testing language can be used for testing the language") {
            it("should return false") {
                (language canBeTestedBy incompatibleTestingLanguage).shouldBeFalse()
            }
        }
    }
})