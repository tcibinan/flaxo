package org.flaxo.model

import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldContainNone
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldStartWith
import org.amshove.kluent.shouldThrow
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
                if (it.isRollbackOnly) transactionManager.rollback(it)
                else transactionManager.commit(it)
            }
        }

        on("user addition") {
            subject.addUser(nickname, password)

            it("should also add credentials for user") {
                subject.getUser(nickname)
                        ?.credentials
                        ?.password shouldEqual password
            }
        }

        on("addition user that already exists") {
            it("should throw an exception") {
                {
                    subject.addUser(nickname, password)
                } shouldThrow EntityAlreadyExistsException::class
            }
        }

        on("getting non-existing user") {
            it("should return null") {
                subject.getUser("non-existing").shouldBeNull()
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

                user.credentials.githubToken shouldEqual githubToken
                user.credentials.travisToken shouldEqual travisToken
                user.credentials.codacyToken shouldEqual codacyToken
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
                course.language shouldEqual language
                course.testingLanguage shouldEqual testLanguage
                course.testingFramework shouldEqual testingFramework
            }

            it("should create necessary amount of tasks") {
                tasks.count() shouldEqual numberOfTasks
            }

            it("should create tasks with ordered numbers in the titles") {
                tasks.map { it.branch }
                        .sorted()
                        .mapIndexed { index, name -> Pair((index + 1).toString(), name) }
                        .forEach { (taskIndex, taskName) ->
                            taskName shouldContain taskIndex
                        }
            }

            it("should create tasks with the given tasks prefix in the titles") {
                tasks.forEach { it.branch shouldStartWith tasksPrefix }
            }

            it("should create tasks without plagiarism reports") {
                tasks.forEach { it.plagiarismReports.shouldBeEmpty() }
            }

            it("should have default state") {
                course.state.lifecycle shouldEqual CourseLifecycle.INIT
                course.state.activatedServices.shouldBeEmpty()
            }
        }

        on("addition course with name that already exists for the user") {
            val owner = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")

            it("should throw an exception") {
                {
                    subject.createCourse(
                            courseName = courseName,
                            language = language,
                            testingLanguage = testLanguage,
                            testingFramework = testingFramework,
                            tasksPrefix = tasksPrefix,
                            numberOfTasks = numberOfTasks,
                            owner = owner
                    )
                } shouldThrow EntityAlreadyExistsException::class
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
                        .count() shouldEqual 2
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
                updatedCourse.state.lifecycle shouldEqual CourseLifecycle.RUNNING
            }

            it("should change its state activated services") {
                updatedCourse.state.activatedServices shouldContainAll listOf(
                        IntegratedService.GITHUB,
                        IntegratedService.TRAVIS
                )
                updatedCourse.state.activatedServices shouldContainNone listOf(
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
                plagiarismReport.shouldNotBeNull()
            }

            it("should contain plagiarism report url") {
                plagiarismReport!!.url shouldEqual plagiarismUrl
            }

            it("should contain plagiarism report url") {
                plagiarismReport!!.matches shouldEqual matches
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
                buildReport.shouldNotBeNull()
            }

            it("should contain build report succeed status") {
                buildReport!!.succeed shouldEqual buildSucceed
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
                codeStyleReport.shouldNotBeNull()
            }

            it("should contain code style report grade") {
                codeStyleReport!!.grade shouldEqual codeStyleGrade
            }
        }

        on("course deletion") {
            val user = subject.getUser(nickname)
                    ?: throw EntityNotFound("User $nickname")
            subject.deleteCourse(courseName, user)

            it("should delete the course") {
                subject.getCourse(courseName, user).shouldBeNull()
            }
        }

    }

})