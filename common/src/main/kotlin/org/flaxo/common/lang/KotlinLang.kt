package org.flaxo.common.lang

import org.flaxo.common.framework.JUnitTestingFramework
import org.flaxo.common.framework.SpekTestingFramework

/**
 * Kotlin language.
 */
object KotlinLang : AbstractLanguage("kotlin",
        extensions = setOf("kt", "kts"),
        testingLanguages = setOf(Itself),
        testingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)
