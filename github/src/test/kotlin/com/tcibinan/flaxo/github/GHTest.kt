package com.tcibinan.flaxo.github

import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object GHTest: Spek({
    describe("github") {
        on("something") {
            it("should return 5") {
                5 shouldBe 5
            }
        }
        on("anything") {
            it("should return 7") {
                7 shouldBe 7
            }
        }
    }
})