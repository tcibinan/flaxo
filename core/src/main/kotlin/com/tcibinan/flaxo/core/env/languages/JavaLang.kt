package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.frameworks.JUnitTestingFramework

object JavaLang : AbstractLanguage("java",
        suitableTestLanguages = setOf(KotlinLang),
        suitableTestingFrameworks = setOf(JUnitTestingFramework)
)