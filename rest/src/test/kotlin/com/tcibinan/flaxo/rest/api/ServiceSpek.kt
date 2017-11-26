package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.rest.Application
import com.tcibinan.flaxo.rest.model.Calculator
import com.tcibinan.flaxo.rest.model.Echo
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

object ServiceSpek: SubjectSpek<String>({
    val context = AnnotationConfigApplicationContext(Application::class.java)
    val calc = context.getBean("calculator", Calculator::class.java)

    describe("echoing") {
        val message = "some message"
        val expected = Echo(message)

        on("calling with message=$message") {
            val actual = EchoController().echo(message)
            it("should return $expected") {
                actual shouldEqual expected
            }
        }

        on("calling calculator") {
            it("should return 5") {
                calc() shouldBe 5
            }
        }
    }
})