package org.flaxo.core.lang

import org.flaxo.core.framework.BashInputOutputTestingFramework

/**
 * Bash language.
 */
object BashLang : AbstractLanguage(
        name = "bash",
        extensions = setOf("sh", "bash"),
        testingLanguages = setOf(Itself),
        testingFrameworks = setOf(BashInputOutputTestingFramework)
)
