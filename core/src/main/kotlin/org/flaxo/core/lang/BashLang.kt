package org.flaxo.core.lang

import org.flaxo.core.framework.BashInputOutputTestingFramework

/**
 * Bash language.
 */
object BashLang : AbstractLanguage(
        name = "bash",
        extensions = setOf("sh", "bash"),
        testingLanguages = emptySet(),
        testingFrameworks = setOf(BashInputOutputTestingFramework)
)
