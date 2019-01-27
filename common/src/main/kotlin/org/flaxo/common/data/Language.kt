package org.flaxo.common.data

/**
 * Programming language.
 */
class Language(
        /**
         * Language name.
         */
        val name: String,

        /**
         * List of compatible testing language names.
         */
        val compatibleTestingLanguages: List<String>,

        /**
         * List of compatible testing framework names.
         */
        val compatibleTestingFrameworks: List<String>
)
