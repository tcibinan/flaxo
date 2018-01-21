package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.model.CourseStatus
import io.kotlintest.matchers.haveSubstring
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.matchers.shouldThrow
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext

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
            val tasks = subject.getTasks(course)

            it("should contain the course") {
                course.language shouldBe language
                course.testLanguage shouldBe testLanguage
                course.testingFramework shouldBe testingFramework
            }

            it("should also create necessary amount of tasks") {
                tasks.count() shouldBe numberOfTasks
            }

            it("should also create tasks with ordered numbers in the title") {
                tasks.map { it.taskName }
                        .sorted()
                        .mapIndexed { index, name -> Pair((index+1).toString(), name) }
                        .forEach { (taskIndex, taskName) ->
                            taskName should haveSubstring(taskIndex)
                        }
            }

            it("should have default status") {
                course.status shouldBe CourseStatus.INIT
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

            it("should create new entity for each student-task combination") {
                subject.getStudents(course)
                        .map { it.studentTasks }
                        .filter { it.count() == numberOfTasks }
                        .count() shouldBe 2
            }
        }

        on("changing course status") {
            val owner = subject.getUser(nickname)!!
            val course = subject.getCourse(courseName, owner)!!
            val updatedCourse = course.copy(status = CourseStatus.RUNNING)
            subject.updateCourse(updatedCourse)

            it("should change it") {
                subject.getCourse(courseName, owner)!!
                        .status shouldBe CourseStatus.RUNNING
            }
        }

        on("course deletion") {
            val owner = subject.getUser(nickname)!!
            subject.deleteCourse(courseName, owner)

            it("should delete the course") {
                subject.getCourses(nickname)
                        .forEach { it.name shouldNotBe courseName }
            }
        }

    }

})