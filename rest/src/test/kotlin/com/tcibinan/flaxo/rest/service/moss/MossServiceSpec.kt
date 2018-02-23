package com.tcibinan.flaxo.rest.service.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.tcibinan.flaxo.core.language.JavaLang
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.model.data.User
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
    val student1Tasks: Set<StudentTask> = setOf(
            mock {
                on { anyBuilds }.thenReturn(true)
                on { buildSucceed }.thenReturn(true)
                on { task }.thenReturn(
                        mock { on { name }.thenReturn("task1") }
                )
            }
    )
    val student1: Student = mock {
        on { nickname }.thenReturn(student1Name)
        on { studentTasks }.thenReturn(student1Tasks)
    }
    val student2Tasks: Set<StudentTask> = setOf(
            mock {
                on { anyBuilds }.thenReturn(true)
                on { buildSucceed }.thenReturn(false)
                on { task }.thenReturn(
                        mock { on { name }.thenReturn("task1") }
                )
            }
    )
    val student2: Student = mock {
        on { nickname }.thenReturn(student2Name)
        on { studentTasks }.thenReturn(student2Tasks)
    }
    val git: GitInstance = mock {
        on { branches(student1Name, courseName) }
                .thenReturn(listOf(
                        mock {
                            on { name() }.thenReturn("task1")
                            on { files() }.thenReturn(setOf(
                                    mock { on { name() }.thenReturn("A.java") },
                                    mock { on { name() }.thenReturn("B.java") },
                                    mock { on { name() }.thenReturn("C.anotherFile") }
                            ))
                        }
                ))
        on { branches(student1Name, courseName) }
                .thenReturn(listOf(
                        mock {
                            on { name() }.thenReturn("task1")
                            on { files() }.thenReturn(setOf(
                                    mock { on { name() }.thenReturn("A.java") }
                            ))
                        }
                ))
        on { branches(userName, courseName) }
                .thenReturn(listOf(
                        mock {
                            on { name() }.thenReturn("task1")
                            on { files() }.thenReturn(setOf(
                                    mock { on { name() }.thenReturn("A.java") }
                            ))
                        }
                ))
    }
    val gitService: GitService = mock {
        on { with(any()) }.thenReturn(git)
    }
    val user: User = mock {
        on { nickname }.thenReturn(userName)
        on { githubId }.thenReturn("userGithubId")
        on { credentials }.thenReturn(
                mock { on { githubToken }.thenReturn("userGithubToken") }
        )
    }
    val course: Course = mock {
        on { name }.thenReturn(courseName)
        on { user }.thenReturn(user)
        on { students }.thenReturn(setOf(student1, student2))
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

