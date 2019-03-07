package org.flaxo.common.lang

import org.flaxo.common.framework.BashInputOutputTestingFramework
import org.flaxo.common.framework.JUnitTestingFramework
import org.flaxo.common.framework.SpekTestingFramework

/**
 * Kotlin programming language.
 */
object KotlinLang : AbstractLanguage("kotlin", setOf("kt", "kts"),
        testingLanguages = setOf(Itself),
        testingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework))

/**
 * Java programming language.
 */
object JavaLang : AbstractLanguage("java", setOf("java"),
        testingLanguages = setOf(Itself, KotlinLang),
        testingFrameworks = setOf(JUnitTestingFramework))

/**
 * Bash programming language.
 */
object BashLang : AbstractLanguage("bash", setOf("sh", "bash"),
        testingLanguages = emptySet(),
        testingFrameworks = setOf(BashInputOutputTestingFramework))

/**
 * C++ programming language.
 */
object CppLang : AbstractLanguage("c++", setOf("cc", "cpp"),
        testingLanguages = setOf(BashLang))

/**
 * C programming language.
 */
object CLang : AbstractLanguage("c", setOf("c"))

/**
 * Scala programming language.
 */
object ScalaLang : AbstractLanguage("scala", setOf("scala"))

/**
 * R programming language.
 */
object RLang : AbstractLanguage("R", setOf("R"))

/**
 * Python programming language.
 */
object PythonLang : AbstractLanguage("python", setOf("py"))

/**
 * Haskell programming language.
 */
object HaskellLang : AbstractLanguage("haskell", setOf("sk"))

/**
 * Javascript programming language.
 */
object JavascriptLang : AbstractLanguage("javascript", setOf("js"))

/**
 * Rust programming language.
 */
object RustLang : AbstractLanguage("rust", setOf("rs"))
