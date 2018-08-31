package org.flaxo.core.language

/**
 * C++ language.
 */
object `C++Lang` : AbstractLanguage(
        name = "c++",
        extensions = setOf("cpp"),
        compatibleTestingLanguages = setOf(BashLang),
        canBeTestedByTheLanguageItself = false,
        compatibleTestingFrameworks = emptySet()
)
