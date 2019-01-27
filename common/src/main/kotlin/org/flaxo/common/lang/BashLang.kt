package org.flaxo.common.lang

import org.flaxo.common.framework.BashInputOutputTestingFramework

/**
 * Bash language.
 */
object BashLang : AbstractLanguage(
        name = "bash",
        extensions = setOf("sh", "bash"),
        testingLanguages = emptySet(),
        testingFrameworks = setOf(BashInputOutputTestingFramework)
)
