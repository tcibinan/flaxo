package org.flaxo.core.language

import org.flaxo.core.framework.JUnitTestingFramework

/**
 * Java language.
 */
object JavaLang : AbstractLanguage("java",
        extensions = setOf("java"),
        compatibleTestingLanguages = setOf(KotlinLang),
        compatibleTestingFrameworks = setOf(JUnitTestingFramework)
)
