package org.flaxo.core.lang

import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework

/**
 * Kotlin language.
 */
object KotlinLang : AbstractLanguage("kotlin",
        extensions = setOf("kt", "kts"),
        testingLanguages = setOf(Itself),
        testingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)
