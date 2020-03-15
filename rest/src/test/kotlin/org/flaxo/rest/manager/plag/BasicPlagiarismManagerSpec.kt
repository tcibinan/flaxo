package org.flaxo.rest.manager.plag

import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotBeNullOrBlank
import org.amshove.kluent.shouldThrow
import org.flaxo.common.NotFoundException
import org.flaxo.common.data.plagiarism.PlagiarismGraph
import org.flaxo.common.data.plagiarism.PlagiarismLink
import org.flaxo.common.data.plagiarism.PlagiarismNode
import org.flaxo.model.DataManager
import org.flaxo.model.data.Course
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.PlagiarismReport
import org.flaxo.model.data.Task
import org.flaxo.model.data.User
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.PlagiarismReportNotFoundException
import org.flaxo.rest.manager.TaskNotFoundException
import org.flaxo.rest.manager.UserNotFoundException
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

class BasicPlagiarismManagerSpec : SubjectSpek<PlagiarismManager>({
    val user = User(name = "user")
    val student1 = "student1"
    val student2 = "student2"
    val student3 = "student3"
    val graph = PlagiarismGraph(
            nodes = listOf(
                    PlagiarismNode(student1),
                    PlagiarismNode(student2),
                    PlagiarismNode(student3)
            ),
            links = listOf(
                    PlagiarismLink(first = student1, second = student2, weight = 50),
                    PlagiarismLink(first = student2, second = student3, weight = 75)
            )
    )
    val anotherGraph = PlagiarismGraph(nodes = emptyList(), links = emptyList())
    val matches: List<PlagiarismMatch> = listOf(
            PlagiarismMatch(student1 = student1, student2 = student2, percentage = 50),
            PlagiarismMatch(student1 = student2, student2 = student3, percentage = 75)
    )
    val report = PlagiarismReport(id = 1, matches = matches)
    val emptyReport = PlagiarismReport(id = 2)
    val anotherReport = PlagiarismReport(id = 3)
    val task = Task(id = 1, branch = "branch", plagiarismReports = listOf(emptyReport, report))
    val anotherTask = Task(id = 2, branch = "anotherBranch", plagiarismReports = listOf(anotherReport))
    val taskWithoutReports = Task(id = 3, branch = "branchWithoutReports")
    val course = Course(name = "course", tasks = setOf(task, anotherTask, taskWithoutReports))

    val dataManager: DataManager = mock {
        on { getUser(user.name) } doReturn (user)
        on { getCourse(course.name, user) } doReturn (course)
        on { getPlagiarismReport(report.id) } doReturn (report)
        on { getPlagiarismReport(anotherReport.id) } doReturn (anotherReport)
    }
    val plagiarismAnalysisManager: PlagiarismAnalysisManager = mock {}

    subject { BasicPlagiarismManager(dataManager, plagiarismAnalysisManager) }

    describe("plagiarism manager") {
        describe("plagiarism analysis") {
            on("analysing plagiarism for non existing user") {
                it("should throw") {
                    {
                        subject.analyse("invalid", course.name, task.branch)
                    } shouldThrow UserNotFoundException::class
                }
            }

            on("analysing plagiarism for non existing course") {
                it("should throw") {
                    {
                        subject.analyse(user.name, "invalid", task.branch)
                    } shouldThrow CourseNotFoundException::class
                }
            }

            on("analysing plagiarism for non existing task") {
                it("should throw") {
                    {
                        subject.analyse(user.name, course.name, "invalid")
                    } shouldThrow TaskNotFoundException::class
                }
            }

            on("analysing plagiarism") {
                subject.analyse(user.name, course.name, task.branch)
                it("should call delegate analysis to moss plagiarism manager") {
                    verify(plagiarismAnalysisManager).analyse(task)
                }
            }
        }

        describe("graph access token generation") {
            on("generating graph access token for non existing user") {
                it("should throw") {
                    {
                        subject.generateGraphAccessToken("invalid", course.name, task.branch)
                    } shouldThrow UserNotFoundException::class
                }
            }

            on("generating graph access token for non existing course") {
                it("should throw") {
                    {
                        subject.generateGraphAccessToken(user.name, "invalid", task.branch)
                    } shouldThrow CourseNotFoundException::class
                }
            }

            on("generating graph access token for non existing task") {
                it("should throw") {
                    {
                        subject.generateGraphAccessToken(user.name, course.name, "invalid")
                    } shouldThrow TaskNotFoundException::class
                }
            }

            on("generating graph access token if there are no plagiarism reports") {
                it("should throw") {
                    {
                        subject.generateGraphAccessToken(user.name, course.name, taskWithoutReports.branch)
                    } shouldThrow PlagiarismReportNotFoundException::class
                }
            }

            on("generating graph access token") {
                val token = subject.generateGraphAccessToken(user.name, course.name, task.branch)
                it("should return non-empty access token") {
                    token.shouldNotBeNullOrBlank()
                }

                it("should return access token for the latest plagiarism report") {
                    subject.getGraph(token) shouldEqual graph
                }
            }
        }

        describe("retrieving graph by access token") {
            on("getting graph by invalid access token") {
                it("should throw") {
                    { subject.getGraph("invalid") } shouldThrow NotFoundException::class
                }
            }

            on("getting graph by previously generated access token") {
                val token = subject.generateGraphAccessToken(user.name, course.name, task.branch)
                it("should return graph") {
                    subject.getGraph(token) shouldEqual graph
                }
            }

            on("getting graph by different tokens of the same graph") {
                val token1 = subject.generateGraphAccessToken(user.name, course.name, task.branch)
                val token2 = subject.generateGraphAccessToken(user.name, course.name, task.branch)
                it("should return the same graph") {
                    subject.getGraph(token1) shouldEqual graph
                    subject.getGraph(token2) shouldEqual graph
                }
            }

            on("getting graph by different tokens of different graphs") {
                val token1 = subject.generateGraphAccessToken(user.name, course.name, task.branch)
                val token2 = subject.generateGraphAccessToken(user.name, course.name, anotherTask.branch)
                it("should return different graphs") {
                    subject.getGraph(token1) shouldEqual graph
                    subject.getGraph(token2) shouldEqual anotherGraph
                }
            }
        }
    }
})
