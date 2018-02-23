package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.JUnitTestingFramework

object JavaLang : AbstractLanguage("java", "java",
        suitableTestLanguages = setOf(KotlinLang),
        suitableTestingFrameworks = setOf(JUnitTestingFramework)
)