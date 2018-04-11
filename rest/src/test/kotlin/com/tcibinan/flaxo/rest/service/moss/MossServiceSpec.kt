package com.tcibinan.flaxo.rest.service.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.RemoteEnvironmentFile
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Credentials
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.Solution
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User
import com.tcibinan.flaxo.moss.Moss
import com.tcibinan.flaxo.rest.service.git.GitService
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
    val task1 = "task1"

    val supportedLanguages: Map<String, Language> = mapOf("java" to JavaLang)
    val solutionEntities1: Set<Solution> = setOf(
            Solution(built = true, succeed = true,
                    task = Task(name = task1)
            )
    )
    val student1 = Student(nickname = student1Name, solutions = solutionEntities1)
    val solutionEntities2: Set<Solution> = setOf(
            Solution(built = true, succeed = false,
                    task = Task(name = task1)
            )
    )
    val student2 = Student(nickname = student2Name, solutions = solutionEntities2)
    val user = User(nickname = userName, githubId = userGithubId,
            credentials = Credentials(githubToken = "userGithubToken")
    )
    val course = Course(name = courseName, user = user, language = language,
            students = setOf(student1, student2)
    )
    val git: Git = mock {
        val student1Branches = listOf(
                branch(task1, emptyFile(student1SolutionFile), emptyFile(student1ExtraFile))
        )
        val student2Branches = listOf(
                branch(task1, emptyFile(student2SolutionFile))
        )
        val userBranches = listOf(
                branch(task1, emptyFile(userTaskFile))
        )
        on { branches(student1Name, courseName) }.thenReturn(student1Branches)
        on { branches(student2Name, courseName) }.thenReturn(student2Branches)
        on { branches(userGithubId, courseName) }.thenReturn(userBranches)
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
                assertTrue { mossTasks.any { it.taskName == "$userName/$courseName/$task1" } }
            }

            it("should only contain tasks where is at least one succeed solutions") {
                val task: MossTask = mossTasks.find { it.taskName.endsWith(task1) }
                        ?: throw MossTaskNotFound("Moss task with postfix $task1 not found")

                assertTrue { task.solutions.any(filesWithFileNameOf(student1SolutionFile)) }
                assertFalse { task.solutions.any(filesWithFileNameOf(student2SolutionFile)) }
            }

            it("should only contain tasks with solutions on the proper language") {
                mossTasks.flatMap { it.solutions }
                        .also {
                            assertTrue { it.all { it.name().endsWith("java") } }
                        }
            }

            it("should contains tasks where each solution file have student's name as a root folder") {
                val task: MossTask = mossTasks.first()
                val solutions: List<EnvironmentFile> = task.solutions

                assertTrue { solutions.size == 1 }
                assertTrue { solutions.any { it.name().split("/").first() == student1Name } }
            }

            it("should contains tasks where each base file have 'base' as a root folder") {
                val task: MossTask = mossTasks.first()
                val base: List<EnvironmentFile> = task.base

                assertTrue { base.isNotEmpty() }
                assertTrue { base.all { it.name().split("/").first() == "base" } }
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
        { it.name().endsWith(Paths.get(filePath).fileName.toString()) }

