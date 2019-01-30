package org.flaxo.common.data

/**
 * Course settings.
 */
class CourseSettings(
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
)
