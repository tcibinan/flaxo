package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.JUnitTestingFramework

/**
 * Java language object.
 */
object JavaLang : AbstractLanguage("java", "java",
        suitableTestLanguages = setOf(KotlinLang),
        suitableTestingFrameworks = setOf(JUnitTestingFramework)
)