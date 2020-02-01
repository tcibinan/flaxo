package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.Identifiable

/**
 * Course settings.
 */
@Serializable
data class CourseSettings(

        override val id: Long,

        /**
         * Main programming language.
         */
        val language: String?,

        /**
         * Testing programming language.
         */
        val testingLanguage: String?,

        /**
         * Testing programming language framework.
         */
        val testingFramework: String?,

        /**
         * File pattern for plagiarism analysis.
         */
        val plagiarismFilePattern: String?,

        /**
         * Plagiarism analysis backend.
         */
        val plagiarismBackend: PlagiarismBackend,

        /**
         * Flag that enables notifications on solution score update.
         */
        val notificationOnScoreChange: Boolean,

        /**
         * Notification template that will be used to notify student on solution score update.
         */
        val scoreChangeNotificationTemplate: String?
) : Identifiable
