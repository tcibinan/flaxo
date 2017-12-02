package com.tcibinan.flaxo.core

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldThrow
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.dao.DataIntegrityViolationException

class DataServiceTest : SubjectSpek<DataService>({
    val nickname = "nickname"
    val anotherNickname = "anotherNickname"
    val password = "password"
    val courseName = "course"
    val language = "laguage"
    val testLanguage = "testLanguage"
    val testingFramework = "testingFramework"
    val numberOfTasks = 4
    val student = "student"
    val anotherStudent = "anotherStudent"

    val context = AnnotationConfigApplicationContext(JpaTestApplication::class.java)

    subject { context.getBean("dataService", DataService::class.java) }

    describe("data service") {

        on("user addition") {
            subject.addUser(nickname, password)
            it("should contain the user") {
                subject.getUser(nickname)?.credentials?.password shouldBe password
            }
        }

        on("addition user that already exists") {
            it("should throw an exception") {
                shouldThrow<EntityAlreadyExistsException> { subject.addUser(nickname, password) }
            }
        }

        on("getting non-existing user") {
            it("should return null") {
                subject.getUser("non-existing") shouldBe null
            }
        }

        on("course creation") {
            val owner = subject.getUser(nickname)!!
            val course = subject.createCourse(courseName, language, testLanguage, testingFramework, numberOfTasks, owner)

            it("should contain the course") {
                course.language shouldBe language
                course.testLanguage shouldBe testLanguage
                course.testingFramework shouldBe testingFramework
            }
            it("should also create necessary tasks") {
                subject.getTasks(course).count() shouldBe numberOfTasks
            }
        }

        on("addition course with name that already exists for the user") {
            val owner = subject.getUser(nickname)!!
            it("should throw an exception") {
                shouldThrow<EntityAlreadyExistsException> {
                    subject.createCourse(courseName, language, testLanguage, testingFramework, numberOfTasks, owner)
                }
            }
        }

        on("addition course with name that already exists for another the user") {
            val anotherUser = subject.addUser(anotherNickname, password)
            it("shouldn't throw an exception") {
                subject.createCourse(courseName, language, testLanguage, testingFramework, numberOfTasks, anotherUser)
            }
        }

        on("addition students to the course") {
            val owner = subject.getUser(nickname)!!
            val course = subject.getCourse(courseName, owner)!!
            subject.addStudent(student, course)
            subject.addStudent(anotherStudent, course)
            it("should add all the student to the course") {
                val studentsNames = subject.getStudents(course).map { it.nickname }.toSet()
                studentsNames shouldEqual setOf(student, anotherStudent)
            }
        }

    }

})