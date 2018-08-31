package org.flaxo.core.lang

import org.flaxo.core.framework.TestingFramework

/**
 * Abstract language.
 */
abstract class AbstractLanguage(

        override val name: String,

        override val extensions: Set<String>,

        /**
         * You can't use a declaring language in the list of testing languages.
         * Use [Itself] object to represent the same language.
         */
        private val testingLanguages: Set<Language>,

        testingFrameworks: Set<TestingFramework>
) : Language {

    override val compatibleTestingLanguages: Set<Language>
        get() =
            if (Itself in testingLanguages) testingLanguages - Itself + this
            else testingLanguages

    override val compatibleTestingFrameworks: Set<TestingFramework> = testingFrameworks

    override fun toString() = name
}
