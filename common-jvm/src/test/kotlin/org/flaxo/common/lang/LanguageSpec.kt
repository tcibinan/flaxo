package org.flaxo.common.lang

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object LanguageSpec : Spek({

    describe("language that can be used for testing") {
        val language = Language.Kotlin
        val testingFrameworks = setOf(Framework.JUnit, Framework.Spek)
        val incompatibleTestingFramework = Framework.BashIO

        on("getting testing frameworks") {
            it("should return list of testing frameworks") {
                language.testingFrameworks.shouldContainAll(testingFrameworks)
            }
        }

        on("checking if any of the supported testing frameworks works with the language") {
            it("should return true") {
                testingFrameworks.forEach { language.worksWith(it).shouldBeTrue() }
            }
        }

        on("checking if an incompatible testing framework works with the language") {
            it("should return false") {
                language.worksWith(incompatibleTestingFramework).shouldBeFalse()
            }
        }
    }

    describe("language that cannot be used for testing") {
        val language = Language.Cpp

        on("getting testing frameworks") {
            it("should return empty list") {
                language.testingFrameworks.shouldBeEmpty()
            }
        }

        on("checking if any of testing frameworks works with the language") {
            it("should return false") {
                Framework.values().forEach { language.worksWith(it).shouldBeFalse() }
            }
        }
    }

    describe("language that can be tested") {
        val language = Language.Kotlin
        val testingLanguages = setOf(Language.Kotlin)
        val incompatibleTestingLanguages = Language.values().toSet() - testingLanguages

        on("getting testing languages") {
            it("should return list of testing languages") {
                language.testingLanguages shouldEqual testingLanguages
            }
        }

        on("checking if any of the supported testing languages can be used for testing the language") {
            it("should return true") {
                testingLanguages.forEach { language.canBeTestedBy(it).shouldBeTrue() }
            }
        }

        on("checking if incompatible testing languages can be used for testing the language") {
            it("should return false") {
                incompatibleTestingLanguages.forEach { language.canBeTestedBy(it).shouldBeFalse() }
            }
        }
    }

    describe("language that cannot be tested") {
        val language = Language.Bash

        on("getting testing languages") {
            it("should return empty list") {
                language.testingLanguages.shouldBeEmpty()
            }
        }

        on("checking if any of languages can be used for testing the language") {
            it("should return false") {
                Language.values().forEach { language.canBeTestedBy(it).shouldBeFalse() }
            }
        }
    }

    describe("language that can be tested by itself") {
        val language = Language.Kotlin

        on("checking if the language can be used for testing itself") {
            it("should return true") {
                language.canBeTestedBy(language).shouldBeTrue()
            }
        }
    }

    describe("language that cannot be tested by itself") {
        val language = Language.Cpp

        on("checking if the language can be used for testing itself") {
            it("should return false") {
                language.canBeTestedBy(language).shouldBeFalse()
            }
        }
    }
})
