package org.flaxo.common.lang

import org.flaxo.common.framework.JUnitTestingFramework

/**
 * Java language.
 */
object JavaLang : AbstractLanguage("java",
        extensions = setOf("java"),
        testingLanguages = setOf(Itself, KotlinLang),
        testingFrameworks = setOf(JUnitTestingFramework)
)
