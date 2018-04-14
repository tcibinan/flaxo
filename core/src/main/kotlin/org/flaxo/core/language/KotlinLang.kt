package org.flaxo.core.language

import org.flaxo.core.framework.JUnitTestingFramework
import org.flaxo.core.framework.SpekTestingFramework

/**
 * Kotlin language object.
 */
object KotlinLang : AbstractLanguage("kotlin",
        extensions = setOf("kt"),
        compatibleTestingLanguages = setOf(),
        compatibleTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)