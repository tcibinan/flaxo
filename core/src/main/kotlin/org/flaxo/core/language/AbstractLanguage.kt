package org.flaxo.core.language

import org.flaxo.core.framework.TestingFramework

/**
 * Abstract language class.
 */
abstract class AbstractLanguage(override val name: String,
                                override val extensions: Set<String>,
                                compatibleTestingLanguages: Set<Language>,
                                override val compatibleTestingFrameworks: Set<TestingFramework>
) : Language {

    override val compatibleTestingLanguages: Set<Language> = compatibleTestingLanguages
        get() = field + this

    override fun toString() = name
}