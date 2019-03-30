package org.flaxo.cpp

import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldNotThrow
import org.amshove.kluent.shouldThrow
import org.flaxo.common.Framework
import org.flaxo.common.Language
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object CppEnvironmentSupplierSpec : Spek({
    val travisWebHookUrl = "travisWebHookUrl"

    describe("c++ environment supplier") {
        on("initialization with an unsupported language") {
            it("should throw an exception") {
                {
                    CppEnvironmentSupplier(Language.Java, Language.Bash, Framework.BashIO, travisWebHookUrl)
                } shouldThrow CppEnvironmentException::class
            }
        }

        on("initialization with an unsupported testing language") {
            it("should throw an exception") {
                {
                    CppEnvironmentSupplier(Language.Cpp, Language.Java, Framework.BashIO, travisWebHookUrl)
                } shouldThrow CppEnvironmentException::class
            }
        }

        on("initialization with an unsupported testing framework") {
            it("should throw an exception") {
                {
                    CppEnvironmentSupplier(Language.Cpp, Language.Bash, Framework.JUnit, travisWebHookUrl)
                } shouldThrow CppEnvironmentException::class
            }
        }

        on("initialization with supported technologies") {
            it("should no throw exception") {
                {
                    CppEnvironmentSupplier(Language.Cpp, Language.Bash, Framework.BashIO, travisWebHookUrl)
                } shouldNotThrow CppEnvironmentException::class
            }
        }

        on("getting environment for C++, Bash, IO tests") {
            val supplier = CppEnvironmentSupplier(Language.Cpp, Language.Bash, Framework.BashIO,
                    travisWebHookUrl)
            val environment = supplier.environment()

            it("should produce CppBashEnvironment") {
                environment shouldBeInstanceOf CppBashEnvironment::class
            }
        }
    }
})
