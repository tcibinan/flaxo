package org.flaxo.core.lang

import org.flaxo.core.framework.TestingFramework

/**
 * The language itself.
 *
 * Represents the same language that is being used in the current context.
 */
internal object Itself : Language {
    override val extensions: Set<String>
        get() = throw UnsupportedOperationException("'Itself' language is not an actual language.")
    override val compatibleTestingLanguages: Set<Language>
        get() = throw UnsupportedOperationException("'Itself' language is not an actual language.")
    override val compatibleTestingFrameworks: Set<TestingFramework>
        get() = throw UnsupportedOperationException("'Itself' language is not an actual language.")
    override val name: String
        get() = throw UnsupportedOperationException("'Itself' language is not an actual language.")
}