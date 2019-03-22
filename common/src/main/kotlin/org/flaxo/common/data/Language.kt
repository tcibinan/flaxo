package org.flaxo.common.data

/**
 * Programming language.
 */
data class Language(

        override val name: String,

        /**
         * List of compatible testing language names.
         */
        val compatibleTestingLanguages: List<String>,

        /**
         * List of compatible testing framework names.
         */
        val compatibleTestingFrameworks: List<String>
): Named
