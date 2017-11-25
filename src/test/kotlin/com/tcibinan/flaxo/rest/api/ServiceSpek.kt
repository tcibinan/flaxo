package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.model.Echo
import io.kotlintest.matchers.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object ServiceSpek: SubjectSpek<String>({
    describe("echoing") {
        val message = "some message"
        val expected = Echo(message)
        on("calling with message=$message") {
            val actual = EchoController().echo(message)
            it("should return $expected") {
                actual shouldEqual expected
            }
        }
    }
})