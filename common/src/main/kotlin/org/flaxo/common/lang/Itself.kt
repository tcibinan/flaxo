package org.flaxo.common.lang

import org.flaxo.common.framework.TestingFramework

private const val ERROR_MESSAGE = "'Itself' language is not an actual language."

/**
 * The language itself.
 *
 * [Itself] object is **not an actual programming language**.
 *
 * Represents the same language that is being used in the current context.
 */
internal object Itself : Language {

    override val extensions: Set<String>
        get() = throw UnsupportedOperationException(ERROR_MESSAGE)

    override val compatibleTestingLanguages: Set<Language>
        get() = throw UnsupportedOperationException(ERROR_MESSAGE)

    override val compatibleTestingFrameworks: Set<TestingFramework>
        get() = throw UnsupportedOperationException(ERROR_MESSAGE)

    override val name: String
        get() = throw UnsupportedOperationException(ERROR_MESSAGE)
}
