package org.flaxo.rest.service.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.RemoteEnvironmentFile
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
import org.flaxo.rest.service.git.GitService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Paths
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object MossServiceSpec : SubjectSpek<MossService>({
    val language = "java"
    val userId = "userId"
    val userName = "userName"
    val userGithubId = "userGithubId"
    val courseName = "courseName"
    val student1Name = "student1"
    val userTaskFile = "task/package/Solution.java"
    val student1SolutionFile = "/some/path/student1Solution.java"
    val student1ExtraFile = "/some/path/$student1Name/student1ExtraFile.kt"
    val student2Name = "student2"
    val student2SolutionFile = "/another/package/student2Solution.java"
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
                branch(branch1, emptyFile(userTaskFile))
        )
        on { branches() }.thenReturn(userBranches)
    }
    val student1Repository = mock<Repository> {
        val student1Branches = listOf(
                branch(branch1, emptyFile(student1SolutionFile), emptyFile(student1ExtraFile))
        )
        on { branches() }.thenReturn(student1Branches)
    }
    val student2Repository = mock<Repository> {
        val student2Branches = listOf(
                branch(branch1, emptyFile(student2SolutionFile))
        )
        on { branches() }.thenReturn(student2Branches)
    }
    val git: Git = mock {
        on { getRepository(courseName) }.thenReturn(userRepository)
        on { getRepository(student1Name, courseName) }.thenReturn(student1Repository)
        on { getRepository(student2Name, courseName) }.thenReturn(student2Repository)
    }
    val gitService: GitService = mock {
        on { with(any()) }.thenReturn(git)
    }

    subject { SimpleMossService(userId, gitService, supportedLanguages) }

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
            val mossTasks: List<MossTask> = subject.extractMossTasks(course)

            it("should create non-empty set of tasks") {
                assertTrue { mossTasks.isNotEmpty() }
            }

            it("should contain all tasks from course") {
                assertTrue { mossTasks.size == 1 }
                assertTrue { mossTasks.any { it.taskName == "$userName/$courseName/$branch1" } }
            }

            it("should only contain tasks where is at least one succeed solutions") {
                val task: MossTask = mossTasks.find { it.taskName.endsWith(branch1) }
                        ?: throw MossTaskNotFound("Moss task with postfix $branch1 not found")

                assertTrue { task.solutions.any(filesWithFileNameOf(student1SolutionFile)) }
                assertFalse { task.solutions.any(filesWithFileNameOf(student2SolutionFile)) }
            }

            it("should only contain tasks with solutions on the proper language") {
                mossTasks.flatMap { it.solutions }
                        .also {
                            assertTrue { it.all { it.name.endsWith("java") } }
                        }
            }

            it("should contains tasks where each solution file have student's name as a root folder") {
                val task: MossTask = mossTasks.first()
                val solutions: List<EnvironmentFile> = task.solutions

                assertTrue { solutions.size == 1 }
                assertTrue { solutions.any { it.name.split("/").first() == student1Name } }
            }

            it("should contains tasks where each base file have 'base' as a root folder") {
                val task: MossTask = mossTasks.first()
                val base: List<EnvironmentFile> = task.base

                assertTrue { base.isNotEmpty() }
                assertTrue { base.all { it.name.split("/").first() == "base" } }
            }

            it("should contains tasks where each environment file could be retrieved as a valid java.io.File") {
                val task: MossTask = mossTasks.first()
                val files: List<EnvironmentFile> = task.base + task.solutions

                assertTrue {
                    files.map { it.file() }
                            .map { it.readLines().joinToString("") }
                            .all { it.isNotBlank() }
                }
            }
        }
    }

})

class MossTaskNotFound(taskPostfix: String) : RuntimeException(taskPostfix)

private fun branch(branchName: String, vararg files: EnvironmentFile): Branch {
    return mock {
        on { name }.thenReturn(branchName)
        on { files() }.thenReturn(files.toList())
    }
}

private fun emptyFile(filePath: String) =
        RemoteEnvironmentFile(filePath, "files content".byteInputStream())

private fun filesWithFileNameOf(filePath: String): (EnvironmentFile) -> Boolean =
        { it.name.endsWith(Paths.get(filePath).fileName.toString()) }

