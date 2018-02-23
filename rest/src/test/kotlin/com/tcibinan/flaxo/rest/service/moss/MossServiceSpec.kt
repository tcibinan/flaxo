package com.tcibinan.flaxo.rest.service.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.git.Branch
import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.CredentialsEntity
import com.tcibinan.flaxo.model.entity.StudentEntity
import com.tcibinan.flaxo.model.entity.StudentTaskEntity
import com.tcibinan.flaxo.model.entity.TaskEntity
import com.tcibinan.flaxo.model.entity.UserEntity
import com.tcibinan.flaxo.rest.service.git.GitService
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.test.assertTrue

object MossServiceSpec : SubjectSpek<MossService>({
    val language = "java"
    val userId = "userId"
    val userName = "userName"
    val courseName = "courseName"
    val student1Name = "student1"
    val student2Name = "student2"

    val supportedLanguages: Map<String, Language> = mapOf("java" to JavaLang)
    val studentTaskEntities1: Set<StudentTaskEntity> = setOf(
            StudentTaskEntity().also {
                it.anyBuilds = true
                it.buildSucceed = true
                it.task = TaskEntity().also {
                    it.taskName = "task1"
                }
            }
    )
    val student1 = StudentEntity().also {
        it.nickname = student1Name
        it.studentTasks = studentTaskEntities1
    }
    val studentTaskEntities2: Set<StudentTaskEntity> = setOf(
            StudentTaskEntity().also {
                it.anyBuilds = true
                it.buildSucceed = false
                it.task = TaskEntity().also {
                    it.taskName = "task1"
                }
            }
    )
    val student2 = StudentEntity().also {
        it.nickname = student2Name
        it.studentTasks = studentTaskEntities2
    }
    val user = UserEntity().also {
        val credentials = CredentialsEntity().also {
            it.githubToken = "userGithubToken"
        }
        it.nickname = userName
        it.githubId = "userGithubId"
        it.credentials = credentials
    }
    val course = Course(CourseEntity().also {
        it.name = courseName
        it.user = user
        it.language = language
        it.students = setOf(student1, student2)
    })
    val git: GitInstance = mock {
        val student1Branches: List<Branch> = listOf(
                mock {
                    val file1: EnvironmentFile = mock { on { name() }.thenReturn("A.java") }
                    val file2: EnvironmentFile = mock { on { name() }.thenReturn("B.kt") }
                    on { name() }.thenReturn("task1")
                    on { files() }.thenReturn(setOf(file1, file2))
                }
        )
        val student2Branches: List<Branch> = listOf(
                mock {
                    val file1: EnvironmentFile = mock { on { name() }.thenReturn("A.java") }
                    on { name() }.thenReturn("task1")
                    on { files() }.thenReturn(setOf(file1))
                }
        )
        val student3Branches: List<Branch> = student2Branches
        on { branches(student1Name, courseName) }.thenReturn(student1Branches)
        on { branches(student1Name, courseName) }.thenReturn(student2Branches)
        on { branches(userName, courseName) }.thenReturn(student3Branches)
    }
    val gitService: GitService = mock {
        on { with(any()) }.thenReturn(git)
    }

    subject { SimpleMossService(userId, gitService, supportedLanguages) }

    describe("moss service") {

        on("creating client for $language language with $userId userid") {
            val mossClient = subject.client(language)

            it("should client with the given userid") {
                assertTrue { mossClient.userId == userId }
            }

            it("should client with the given language") {
                assertTrue { mossClient.language == language }
            }
        }

        on("creating moss tasks") {
            val mossTasks = subject.extractMossTasks(course)

            it("should create non-empty set of tasks") {
                assertTrue { mossTasks.isNotEmpty() }
            }
        }
    }

})

