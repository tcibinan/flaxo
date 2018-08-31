package org.flaxo.core.language

import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework

/**
 * Kotlin language.
 */
object KotlinLang : AbstractLanguage("kotlin",
        extensions = setOf("kt", "kts"),
        compatibleTestingLanguages = emptySet(),
        compatibleTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)
