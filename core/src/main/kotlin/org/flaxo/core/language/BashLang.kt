package org.flaxo.core.language

import org.flaxo.core.framework.BashInputOutputTestingFramework

/**
 * Bash language.
 */
object BashLang : AbstractLanguage(
        name = "bash",
        extensions = setOf("sh", "bash"),
        compatibleTestingLanguages = emptySet(),
        compatibleTestingFrameworks = setOf(BashInputOutputTestingFramework)
)
