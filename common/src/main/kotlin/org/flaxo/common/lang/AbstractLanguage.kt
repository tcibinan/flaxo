package org.flaxo.common.lang

import org.flaxo.common.framework.TestingFramework

/**
 * Abstract language.
 */
abstract class AbstractLanguage(

        override val name: String,

        override val extensions: Set<String>,

        /**
         * You can't use a declaring language in the list of its testing languages.
         * Use [Itself] to represent the declaring language instead.
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
