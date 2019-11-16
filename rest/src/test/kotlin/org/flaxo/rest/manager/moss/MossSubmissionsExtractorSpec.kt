package org.flaxo.rest.manager.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LocalFile
import org.flaxo.common.env.file.LazyEnvironmentFile
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.git.Repository
import org.flaxo.model.data.BuildReport
import org.flaxo.model.data.Course
import org.flaxo.model.data.CourseSettings
import org.flaxo.model.data.Credentials
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Student
import org.flaxo.model.data.Task
import org.flaxo.model.data.User
import org.flaxo.moss.MossSubmission
import org.flaxo.rest.manager.github.GithubManager
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.test.assertTrue

object MossSubmissionsExtractorSpec : SubjectSpek<MossSubmissionExtractor>({
    val language = "java"
    val userName = "userName"
    val userGithubId = "userGithubId"
    val courseName = "courseName"
    val student1Name = "student1"
    val userTaskFile = "task/package/Solution.java"
    val student1SolutionFile = "some/path/student1Solution.java"
    val student1ExtraFile = "some/path/$student1Name/student1ExtraFile.kt"
    val student2Name = "student2"
    val student2SolutionFile = "another/package/student2Solution.java"
    val branchName = "branch1"

    val student1Solutions: Set<Solution> = setOf(
            Solution(
                    task = Task(branch = branchName),
                    buildReports = listOf(BuildReport(succeed = true)),
                    student = Student(name = student1Name)
            )
    )
    val student1 = Student(name = student1Name, solutions = student1Solutions)
    val student2Solutions: Set<Solution> = setOf(
            Solution(
                    task = Task(branch = branchName),
                    buildReports = listOf(BuildReport(succeed = false)),
                    student = Student(name = student2Name)
            )
    )
    val student2 = Student(name = student2Name, solutions = student2Solutions)
    val user = User(name = userName, githubId = userGithubId,
            credentials = Credentials(githubToken = "userGithubToken")
    )
    val settings = CourseSettings(language = language)
    val course = Course(name = courseName, user = user, settings = settings, students = setOf(student1, student2))
    val solutions = student1Solutions + student2Solutions
    val task = Task(branch = branchName, course = course, solutions = solutions)
    val userRepository = mock<Repository> {
        val userBranches = listOf(
                branch(branchName, file(userTaskFile))
        )
        on { branches() }.thenReturn(userBranches)
    }
    val student1Repository = mock<Repository> {
        val student1Branches = listOf(
                branch(branchName, file(student1SolutionFile), file(student1ExtraFile))
        )
        on { branches() }.thenReturn(student1Branches)
    }
    val student2Repository = mock<Repository> {
        val student2Branches = listOf(
                branch(branchName, file(student2SolutionFile))
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

    subject { SimpleMossSubmissionsExtractor(githubManager) }

    describe("Moss service") {

        on("extracting moss submission") {
            val submission: MossSubmission = subject.extract(task)

            it("should return submission with filled user, course and task") {
                submission.user shouldEqual userName
                submission.course shouldEqual courseName
                submission.task shouldEqual branchName
            }

            it("should return submission with solution files only from succeed solutions") {
                submission.solutions.any(filesWithFileNameOf(student1SolutionFile)).shouldBeTrue()
                submission.solutions.any(filesWithFileNameOf(student2SolutionFile)).shouldBeFalse()
            }

            it("should return submission with solutions files with the course language extension") {
                submission.solutions.all { it.fileName.endsWith("java") }.shouldBeTrue()
            }

            it("should contain tasks where each solution file have student's name as a root folder") {

                assertTrue { submission.solutions.size == 1 }
                assertTrue { submission.solutions.any { it.localPath.contains(Paths.get(student1Name)) } }
            }

            it("should contain tasks where each base file have 'base' as a root folder") {
                val base: List<LocalFile> = submission.base

                assertTrue { base.isNotEmpty() }
                assertTrue { base.all { it.localPath.contains(Paths.get("base")) } }
            }

            it("should contain tasks where each environment file could be retrieved as a valid file") {
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

private fun branch(branchName: String, vararg files: EnvironmentFile): Branch = mock {
    on { name }.thenReturn(branchName)
    on { files() }.thenReturn(files.toList())
}

private fun file(filePath: String) =
        LazyEnvironmentFile(Paths.get(filePath)) { "files content".byteInputStream() }

private fun filesWithFileNameOf(filePath: String): (EnvironmentFile) -> Boolean = {
    it.fileName == Paths.get(filePath).fileName.toString()
}
