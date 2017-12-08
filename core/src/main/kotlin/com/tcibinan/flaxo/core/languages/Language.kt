package com.tcibinan.flaxo.core.languages

import com.tcibinan.flaxo.core.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.frameworks.JUnit5TestingFramework
import com.tcibinan.flaxo.core.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.frameworks.TestingFramework

abstract class Language(
        val name: String,
        val suitableTestLanguages: Set<Language>,
        val suitableTestingFrameworks: Set<TestingFramework>
)

object JavaLang : Language("java",
        setOf(JavaLang, KotlinLang),
        setOf(JUnit4TestingFramework, JUnit5TestingFramework)
)

object KotlinLang : Language("kotlin",
        setOf(KotlinLang),
        setOf(JUnit4TestingFramework, JUnit5TestingFramework, SpekTestingFramework)
)