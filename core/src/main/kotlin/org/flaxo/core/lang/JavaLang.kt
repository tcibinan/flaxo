package org.flaxo.core.lang

import org.flaxo.core.framework.JUnitTestingFramework

/**
 * Java language.
 */
object JavaLang : AbstractLanguage("java",
        extensions = setOf("java"),
        testingLanguages = setOf(Itself, KotlinLang),
        testingFrameworks = setOf(JUnitTestingFramework)
)
