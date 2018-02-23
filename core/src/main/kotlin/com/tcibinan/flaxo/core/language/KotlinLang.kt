package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.JUnitTestingFramework
import com.tcibinan.flaxo.core.framework.SpekTestingFramework

object KotlinLang : AbstractLanguage("kotlin", "kt",
        suitableTestLanguages = setOf(),
        suitableTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)