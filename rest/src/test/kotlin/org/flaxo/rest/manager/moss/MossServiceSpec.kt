package org.flaxo.rest.manager.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.env.file.RemoteEnvironmentFile
import org.flaxo.core.language.JavaLang
import org.flaxo.core.language.Language
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.git.Repository
import org.flaxo.model.data.BuildReport
import org.flaxo.model.data.Course
import org.flaxo.model.data.Credentials
import org.flaxo.model.data.Student
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Task
import org.flaxo.model.data.User
import org.flaxo.moss.Moss
import org.flaxo.rest.manager.github.GithubManager
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object MossServiceSpec : SubjectSpek<MossManager>({
    val language = "java"
    val userId = "userId"
    val userName = "userName"
    val userGithubId = "userGithubId"
    val courseName = "courseName"
    val student1Name = "student1"
    val userTaskFile = "task/package/Solution.java"
    val student1SolutionFile = "some/path/student1Solution.java"
    val student1ExtraFile = "some/path/$student1Name/student1ExtraFile.kt"
    val student2Name = "student2"
    val student2SolutionFile = "another/package/student2Solution.java"
    val branch1 = "branch1"

    val supportedLanguages: Map<String, Language> = mapOf("java" to JavaLang)
    val solutionEntities1: Set<Solution> = setOf(
            Solution(
                    task = Task(branch = branch1),
                    buildReports = listOf(BuildReport(succeed = true))
            )
    )
    val student1 = Student(nickname = student1Name, solutions = solutionEntities1)
    val solutionEntities2: Set<Solution> = setOf(
            Solution(
                    task = Task(branch = branch1),
                    buildReports = listOf(BuildReport(succeed = false))
            )
    )
    val student2 = Student(nickname = student2Name, solutions = solutionEntities2)
    val user = User(nickname = userName, githubId = userGithubId,
            credentials = Credentials(githubToken = "userGithubToken")
    )
    val course = Course(name = courseName, user = user, language = language,
            students = setOf(student1, student2)
    )
    val userRepository = mock<Repository> {
        val userBranches = listOf(
                branch(branch1, file(userTaskFile))
        )
        on { branches() }.thenReturn(userBranches)
    }
    val student1Repository = mock<Repository> {
        val student1Branches = listOf(
                branch(branch1, file(student1SolutionFile), file(student1ExtraFile))
        )
        on { branches() }.thenReturn(student1Branches)
    }
    val student2Repository = mock<Repository> {
        val student2Branches = listOf(
                branch(branch1, file(student2SolutionFile))
        )
        on { branches() }.thenReturn(student2Branches)
    }
    val git: Git = mock {
        on { getRepository(courseName) }.thenReturn(userRepository)
        on { getRepository(student1Name, courseName) }.thenReturn(student1Repository)
        on { getRepository(student2Name, courseName) }.thenReturn(student2Repository)
    }
    val githubManager: GithubManager = mock {
        on { with(any()) }.thenReturn(git)
    }

    subject { SimpleMossManager(userId, githubManager, supportedLanguages) }

    describe("Moss service") {

        on("creating client for $language language with $userId userid") {
            val mossClient: Moss = subject.client(language)

            it("should client with the given userid") {
                assertTrue { mossClient.userId == userId }
            }

            it("should client with the given language") {
                assertTrue { mossClient.language == language }
            }
        }

        on("creating moss tasks") {
            val mossSubmissions: List<MossSubmission> = subject.extractSubmissions(course)

            it("should create non-empty set of tasks") {
                assertTrue { mossSubmissions.isNotEmpty() }
            }

            it("should contain all tasks from course") {
                assertTrue { mossSubmissions.size == 1 }
                assertTrue {
                    mossSubmissions.any {
                        it.user == userName
                                && it.course == courseName
                                && it.branch == branch1
                    }
                }
            }

            it("should only contain tasks where is at least one succeed solutions") {
                val submission: MossSubmission = mossSubmissions.find { it.branch == branch1 }
                        ?: throw MossTaskNotFound("Moss task with postfix $branch1 not found")

                assertTrue { submission.solutions.any(filesWithFileNameOf(student1SolutionFile)) }
                assertFalse { submission.solutions.any(filesWithFileNameOf(student2SolutionFile)) }
            }

            it("should only contain tasks with solutions on the proper language") {
                mossSubmissions.flatMap { it.solutions }
                        .also { solutions ->
                            assertTrue { solutions.all { it.fileName.endsWith("java") } }
                        }
            }

            it("should contain tasks where each solution file have student's name as a root folder") {
                val submission: MossSubmission = mossSubmissions.first()
                val solutions: List<LocalFile> = submission.solutions

                assertTrue { solutions.size == 1 }
                assertTrue { solutions.any { it.localPath.contains(Paths.get(student1Name)) } }
            }

            it("should contain tasks where each base file have 'base' as a root folder") {
                val submission: MossSubmission = mossSubmissions.first()
                val base: List<LocalFile> = submission.base

                assertTrue { base.isNotEmpty() }
                assertTrue { base.all { it.localPath.contains(Paths.get("base")) } }
            }

            it("should contain tasks where each environment file could be retrieved as a valid file") {
                val submission: MossSubmission = mossSubmissions.first()
                val files: List<LocalFile> = submission.base + submission.solutions

                assertTrue {
                    files.map { it.localPath }
                            .map { Files.readAllLines(it).joinToString("") }
                            .all { it.isNotBlank() }
                }
            }
        }
    }

})

class MossTaskNotFound(taskPostfix: String) : RuntimeException(taskPostfix)

private fun branch(branchName: String, vararg files: EnvironmentFile): Branch = mock {
    on { name }.thenReturn(branchName)
    on { files() }.thenReturn(files.toList())
}

private fun file(filePath: String) =
        RemoteEnvironmentFile(Paths.get(filePath), "files content".byteInputStream())

private fun filesWithFileNameOf(filePath: String): (EnvironmentFile) -> Boolean = {
    it.fileName == Paths.get(filePath).fileName.toString()
}

