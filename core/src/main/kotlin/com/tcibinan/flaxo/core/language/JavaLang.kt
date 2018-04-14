package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.JUnitTestingFramework

/**
 * Java language object.
 */
object JavaLang : AbstractLanguage("java",
        extensions = setOf("java"),
        compatibleTestingLanguages = setOf(KotlinLang),
        compatibleTestingFrameworks = setOf(JUnitTestingFramework)
)