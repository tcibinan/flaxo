package com.tcibinan.flaxo.core

import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class DataServiceTest : SubjectSpek<DataService>({

    val context = AnnotationConfigApplicationContext(JpaTestApplication::class.java)

    subject { context.getBean("dataService", DataService::class.java) }

    describe("data service") {
        val nickname = "nickname"
        val password = "password"

        on("addition of the user") {
            subject.addUser(nickname, password)
            it("should add the user to the database") {
                subject.getUser(nickname)?.credentials?.password shouldBe password
            }
        }

        on("addition of the user that already exists") {
            it("should not throw an exception") {
                subject.addUser(nickname, password)
            }
        }

        on("getting non-existing user") {
            it("should return null") {
                subject.getUser("non-existing") shouldBe null
            }
        }

    }

})