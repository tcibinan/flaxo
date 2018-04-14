package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.framework.SpekTestingFramework

/**
 * Kotlin language object.
 */
object KotlinLang : AbstractLanguage("kotlin",
        extensions = setOf("kt"),
        compatibleTestingLanguages = setOf(),
        compatibleTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)