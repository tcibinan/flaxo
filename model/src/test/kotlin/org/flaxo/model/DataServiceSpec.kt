package org.flaxo.model

import io.kotlintest.matchers.beEmpty
import io.kotlintest.matchers.contain
import io.kotlintest.matchers.containsAll
import io.kotlintest.matchers.haveSubstring
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.matchers.shouldNot
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.matchers.shouldThrow
import io.kotlintest.matchers.startWith
import org.flaxo.model.data.PlagiarismMatch
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.TransactionStatus

class DataServiceSpec : SubjectSpek<DataService>({
    val nickname = "nickname"
    val anotherNickname = "anotherNickname"
    val password = "password"
    val courseName = "course"
    val language = "language"
    val testLanguage = "testLanguage"
    val testingFramework = "testingFramework"
    val tasksPrefix = "task-"
    val numberOfTasks = 4
    val student = "student"
    val anotherStudent = "anotherStudent"
    val plagiarismUrl = "plagiarismUrl"
    val buildSucceed = true
    val codeStyleGrade = "B"

    val context = AnnotationConfigApplicationContext(JpaTestApplication::class.java)
    val transactionManager = context.getBean(PlatformTransactionManager::class.java)

    subject { context.getBean(DataService::class.java) }

    describe("data service") {

        var transactionStatus: TransactionStatus? = null

        beforeEachTest {
            transactionStatus = transactionManager.getTransaction(DefaultTransactionDefinition())
        }

        afterEachTest {
            transactionStatus?.also {
                transactionManager.commit(it)
            }
        }

        on("user addition") {
            subject.addUser(nickname, password)

            it("should also add credentials for user") {
                subject.getUser(nickname)
                        ?.credentials
                        ?.password shouldBe password
            }
        }

        on("addition user that already exists") {
            it("should throw an exception") {
                shouldThrow<EntityAlreadyExistsException> {
                    subject.addUser(nickname, password)
                }
            }
        }

        on("getting non-existing user") {
            it("should return null") {
                subject.getUser("non-existing") shouldBe null
            }
        }

        on("addition tokens to a user") {
            val githubToken = "githubToken"
            val travisToken = "travisToken"
            val codacyToken = "codacyToken"

            subject.addToken(nickname, IntegratedService.CODACY, codacyToken)
            subject.addToken(nickname, IntegratedService.TRAVIS, travisToken)
            subject.addToken(nickname, IntegratedService.GITHUB, githubToken)

            it("should add all tokens to a user credentials") {
                val user = subject.getUser(nickname)
                        ?: throw EntityNotFound("User $nickname")

                user.credentials.githubToken shouldBe githubToken
                user.credentials.travisToken shouldBe travisToken
                user.credentials.codacyToken shouldBe codacyToken
            }
        }

        on("course creation") {
            val owner = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.createCourse(
                    courseName = courseName,
                    language = language,
                    testingLanguage = testLanguage,
                    testingFramework = testingFramework,
                    tasksPrefix = tasksPrefix,
                    numberOfTasks = numberOfTasks,
                    owner = owner
            )
            val tasks = course.tasks

            it("should contain the course") {
                course.language shouldBe language
                course.testingLanguage shouldBe testLanguage
                course.testingFramework shouldBe testingFramework
            }

            it("should create necessary amount of tasks") {
                tasks.count() shouldBe numberOfTasks
            }

            it("should create tasks with ordered numbers in the titles") {
                tasks.map { it.branch }
                        .sorted()
                        .mapIndexed { index, name -> Pair((index + 1).toString(), name) }
                        .forEach { (taskIndex, taskName) ->
                            taskName should haveSubstring(taskIndex)
                        }
            }

            it("should create tasks with the given tasks prefix in the titles") {
                tasks.forEach { it.branch should startWith(tasksPrefix) }
            }

            it("should create tasks without plagiarism reports") {
                tasks.forEach { it.plagiarismReports should beEmpty() }
            }

            it("should have default state") {
                course.state.lifecycle shouldBe CourseLifecycle.INIT
                course.state.activatedServices should beEmpty()
            }
        }

        on("addition course with name that already exists for the user") {
            val owner = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")

            it("should throw an exception") {
                shouldThrow<EntityAlreadyExistsException> {
                    subject.createCourse(
                            courseName = courseName,
                            language = language,
                            testingLanguage = testLanguage,
                            testingFramework = testingFramework,
                            tasksPrefix = tasksPrefix,
                            numberOfTasks = numberOfTasks,
                            owner = owner
                    )
                }
            }
        }

        on("addition course with name that already exists for another the user") {
            val anotherUser = subject.addUser(anotherNickname, password)

            it("should create a course") {
                subject.createCourse(
                        courseName = courseName,
                        language = language,
                        testingLanguage = testLanguage,
                        testingFramework = testingFramework,
                        tasksPrefix = tasksPrefix,
                        numberOfTasks = numberOfTasks,
                        owner = anotherUser
                )
            }
        }

        on("addition students to the course") {
            val owner = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.getCourse(courseName, owner)
                    ?: throw EntityNotFound("Course $courseName")
            subject.addStudent(student, course)
            subject.addStudent(anotherStudent, course)

            it("should add all the student to the course") {
                course.students
                        .map { it.nickname }
                        .toSet() shouldEqual setOf(student, anotherStudent)
            }

            it("should create new entity for each student-task combination") {
                course.students
                        .map { it.solutions }
                        .filter { it.count() == numberOfTasks }
                        .count() shouldBe 2
            }
        }

        on("updating course") {
            val owner = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.getCourse(courseName, owner)
                    ?: throw EntityNotFound("Course $courseName")
            val updatedCourse = course
                    .copy(
                            state = course.state.copy(
                                    lifecycle = CourseLifecycle.RUNNING,
                                    activatedServices = setOf(
                                            IntegratedService.GITHUB,
                                            IntegratedService.TRAVIS
                                    )
                            )
                    )
                    .also { subject.updateCourse(it) }

            it("should change its state lifecycle") {
                updatedCourse.state.lifecycle shouldBe CourseLifecycle.RUNNING
            }

            it("should change its state activated services") {
                updatedCourse.state.activatedServices should containsAll(
                        IntegratedService.GITHUB,
                        IntegratedService.TRAVIS
                )
                updatedCourse.state.activatedServices shouldNot contain(
                        IntegratedService.CODACY
                )
            }
        }

        on("addition plagiarism report to a task") {
            val user = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.getCourse(courseName, user)
                    ?: throw EntityNotFound("Course $courseName")
            val task = course.tasks
                    .firstOrNull()
                    ?: throw EntityNotFound("There is no tasks for course $courseName")
            val matches = listOf(PlagiarismMatch(), PlagiarismMatch())

            subject.addPlagiarismReport(task, plagiarismUrl, matches)

            val plagiarismReport =
                    subject.getTasks(course)
                            .first()
                            .plagiarismReports
                            .lastOrNull()

            it("should contain plagiarism report") {
                plagiarismReport shouldNotBe null
            }

            it("should contain plagiarism report url") {
                plagiarismReport!!.url shouldBe plagiarismUrl
            }

            it("should contain plagiarism report url") {
                plagiarismReport!!.matches shouldBe matches
            }
        }

        on("addition build report to a task") {
            val user = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.getCourse(courseName, user)
                    ?: throw EntityNotFound("Course $courseName")
            val task = course.tasks
                    .firstOrNull()
                    ?: throw EntityNotFound("There is no tasks for course $courseName")
            val solution = task.solutions
                    .firstOrNull()
                    ?: throw EntityNotFound("There is no solution for task in ${task.branch} course $courseName")

            subject.addBuildReport(solution, buildSucceed)

            val buildReport =
                    subject.getSolutions(task)
                            .first()
                            .buildReports
                            .lastOrNull()

            it("should contain build report") {
                buildReport shouldNotBe null
            }

            it("should contain build report succeed status") {
                buildReport!!.succeed shouldBe buildSucceed
            }
        }

        on("addition code style report to a task") {
            val user = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            val course = subject.getCourse(courseName, user)
                    ?: throw EntityNotFound("Course $courseName")
            val task = course.tasks
                    .firstOrNull()
                    ?: throw EntityNotFound("There is no tasks for course $courseName")
            val solution = task.solutions
                    .firstOrNull()
                    ?: throw EntityNotFound("There is no solution for task in ${task.branch} course $courseName")

            subject.addCodeStyleReport(solution, codeStyleGrade)

            val codeStyleReport =
                    subject.getSolutions(task)
                            .first()
                            .codeStyleReports
                            .lastOrNull()

            it("should contain code style report") {
                codeStyleReport shouldNotBe null
            }

            it("should contain code style report grade") {
                codeStyleReport!!.grade shouldBe codeStyleGrade
            }
        }

        on("course deletion") {
            val user = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            subject.deleteCourse(courseName, user)

            it("should delete the course") {
                subject.getCourse(courseName, user) shouldBe null
            }
        }

    }

})