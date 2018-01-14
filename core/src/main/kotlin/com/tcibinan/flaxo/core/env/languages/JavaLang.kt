package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.env.tools.gradle.javaPlugin
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin

object JavaLang : AbstractLanguage("java",
        suitableTestLanguages = setOf(KotlinLang),
        suitableTestingFrameworks = setOf(JUnit4TestingFramework)
) {

    override fun main(buildTool: BuildTool) {
        buildTool.addPlugin(javaPlugin())
    }

    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
    }
}