package org.flaxo.common.data

/**
 * Course settings.
 */
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
        val testingFramework: String?
) : Identifiable
