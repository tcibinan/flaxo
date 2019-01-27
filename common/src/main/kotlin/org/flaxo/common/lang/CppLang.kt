package org.flaxo.common.lang

/**
 * C++ language.
 */
object CppLang : AbstractLanguage(
        name = "c++",
        extensions = setOf("cc", "cpp"),
        testingLanguages = setOf(BashLang),
        testingFrameworks = emptySet()
)
