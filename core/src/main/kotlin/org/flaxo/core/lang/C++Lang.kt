package org.flaxo.core.lang

/**
 * C++ language.
 */
object `C++Lang` : AbstractLanguage(
        name = "c++",
        extensions = setOf("cpp"),
        testingLanguages = setOf(BashLang),
        testingFrameworks = emptySet()
)
