package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.frameworks.JUnitTestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework

object KotlinLang : AbstractLanguage("kotlin",
        suitableTestLanguages = setOf(),
        suitableTestingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework)
)