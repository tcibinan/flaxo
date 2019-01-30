package org.flaxo.common.lang

import org.flaxo.common.framework.BashInputOutputTestingFramework
import org.flaxo.common.framework.JUnitTestingFramework
import org.flaxo.common.framework.SpekTestingFramework

object KotlinLang : AbstractLanguage("kotlin", setOf("kt", "kts"),
        testingLanguages = setOf(Itself),
        testingFrameworks = setOf(JUnitTestingFramework, SpekTestingFramework))
object JavaLang : AbstractLanguage("java", setOf("java"),
        testingLanguages = setOf(Itself, KotlinLang),
        testingFrameworks = setOf(JUnitTestingFramework))
object BashLang : AbstractLanguage("bash", setOf("sh", "bash"),
        testingLanguages = emptySet(),
        testingFrameworks = setOf(BashInputOutputTestingFramework))
object CppLang : AbstractLanguage("c++", setOf("cc", "cpp"),
        testingLanguages = setOf(BashLang))
object CLang : AbstractLanguage("c", setOf("c"))
object ScalaLang : AbstractLanguage("scala", setOf("scala"))
object RLang : AbstractLanguage("R", setOf("R"))
object PythonLang : AbstractLanguage("python", setOf("py"))
object HaskellLang : AbstractLanguage("haskell", setOf("sk"))
object JavascriptLang : AbstractLanguage("javascript", setOf("js"))
object RustLang : AbstractLanguage("rust", setOf("rs"))
