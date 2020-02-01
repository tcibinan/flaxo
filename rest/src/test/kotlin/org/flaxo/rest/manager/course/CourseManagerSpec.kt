package org.flaxo.rest.manager.course

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argThat
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.PlagiarismBackend
import org.flaxo.model.DataManager
import org.flaxo.model.data.Course
import org.flaxo.model.data.User
import org.flaxo.rest.manager.CourseAccessDeniedException
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.UserNotFoundException
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

class CourseManagerSpec : SubjectSpek<CourseManager>({
    val user = User(id = 1, name = "user")
    val course = Course(id = 1, name = "course", user = user)
    val anotherUser = User(id = 2, name = "anotherUser")
    val settings = CourseSettings(id = 1, language = "language", testingLanguage = "testingLanguage",
            testingFramework = "testingFramework", notificationOnScoreChange = false,
            scoreChangeNotificationTemplate = null, plagiarismFilePattern = null,
            plagiarismBackend = PlagiarismBackend.MOSS)
    val anotherSettings = CourseSettings(id = 2, language = "anotherLanguage",
            testingLanguage = "anotherTestingLanguage", testingFramework = "anotherTestingFramework",
            notificationOnScoreChange = false, scoreChangeNotificationTemplate = null,
            plagiarismFilePattern = null, plagiarismBackend = PlagiarismBackend.MOSS)

    val dataManager = mock<DataManager> {
        on { getUser(eq(user.name)) }.thenReturn(user)
        on { getUser(eq(anotherUser.name)) }.thenReturn(anotherUser)
        on { getCourse(eq(course.id)) }.thenReturn(course)
        on { updateCourse(any()) }.thenAnswer { it.arguments[0] }
    }

    subject { BasicCourseManager(dataManager) }

    describe("course manager") {
        describe("course settings update") {
            on("updating course settings for non-existing user") {
                it("should fail") {
                    {
                        subject.updateSettings("non-existing", course.id, settings)
                    } shouldThrow UserNotFoundException::class
                }
            }

            on("updating course settings for non-existing course") {
                it("should fail") {
                    {
                        subject.updateSettings(user.name, -1, settings)
                    } shouldThrow CourseNotFoundException::class
                }
            }

            on("updating course settings for different user's course") {
                it("should fail") {
                    {
                        subject.updateSettings(anotherUser.name, course.id, settings)
                    } shouldThrow CourseAccessDeniedException::class
                }
            }

            on("updating course settings") {
                val updatedCourse = subject.updateSettings(user.name, course.id, anotherSettings)

                it("should return course with updated settings") {
                    updatedCourse.settings.apply {
                        language shouldEqual anotherSettings.language
                        testingLanguage shouldEqual anotherSettings.testingLanguage
                        testingFramework shouldEqual anotherSettings.testingFramework
                    }
                }

                it("should save course with updated settings") {
                    verify(dataManager).updateCourse(argThat {
                        this.settings.run {
                            id == anotherSettings.id &&
                                    language == anotherSettings.language &&
                                    testingLanguage == anotherSettings.testingLanguage &&
                                    testingFramework == anotherSettings.testingFramework
                        }
                    })
                }
            }
        }
    }
})