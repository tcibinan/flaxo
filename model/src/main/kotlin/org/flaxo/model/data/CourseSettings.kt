package org.flaxo.model.data

import org.flaxo.common.Identifiable
import org.flaxo.common.data.PlagiarismBackend
import org.flaxo.model.CourseSettingsView
import java.util.Objects
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Course settings entity.
 */
@Entity(name = "course_settings")
@Table(name = "course_settings")
data class CourseSettings(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val language: String? = null,

        val testingLanguage: String? = null,

        val testingFramework: String? = null,

        val plagiarismFilePattern: String? = null,

        @Enumerated(EnumType.STRING)
        val plagiarismBackend: PlagiarismBackend = PlagiarismBackend.MOSS,

        val notificationOnScoreChange: Boolean = false,

        val scoreChangeNotificationTemplate: String? = null

) : Identifiable, Viewable<CourseSettingsView> {

    override fun view() = CourseSettingsView(
            id = id,
            language = language,
            testingLanguage = testingLanguage,
            testingFramework = testingFramework,
            plagiarismFilePattern = plagiarismFilePattern,
            plagiarismBackend = plagiarismBackend,
            notificationOnScoreChange = notificationOnScoreChange,
            scoreChangeNotificationTemplate = scoreChangeNotificationTemplate
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id
}

fun CourseSettingsView.model(): CourseSettings = CourseSettings(
        id = id,
        language = language,
        testingLanguage = testingLanguage,
        testingFramework = testingFramework,
        plagiarismFilePattern = plagiarismFilePattern,
        plagiarismBackend = plagiarismBackend,
        notificationOnScoreChange = notificationOnScoreChange,
        scoreChangeNotificationTemplate = scoreChangeNotificationTemplate
)
