package org.flaxo.core.language

import org.flaxo.core.framework.TestingFramework

/**
 * Abstract language.
 */
abstract class AbstractLanguage(override val name: String,
                                override val extensions: Set<String>,
                                override val compatibleTestingLanguages: Set<Language>,
                                override val compatibleTestingFrameworks: Set<TestingFramework>,
                                private val canBeTestedByTheLanguageItself: Boolean = true
) : Language {

    override fun toString() = name

    override fun canBeTestedBy(testingLanguage: Language): Boolean =
            super.canBeTestedBy(testingLanguage)
                    || (canBeTestedByTheLanguageItself && testingLanguage == this)
}
