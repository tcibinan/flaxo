package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.UnsupportedBuildToolException
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.GradleBuildTool
import com.tcibinan.flaxo.core.env.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.env.tools.javaPlugin
import com.tcibinan.flaxo.core.env.tools.junitPlatformPlugin

object JavaLang : Language("java",
        suitableTestLanguages = setOf(JavaLang, KotlinLang),
        suitableTestingFrameworks = setOf(JUnit4TestingFramework)
) {

    override fun main(buildTool: BuildTool) {
        when (buildTool) {
            is GradleBuildTool -> buildTool.addPlugin(javaPlugin())
            else -> throw UnsupportedBuildToolException(JavaLang, buildTool)
        }
    }

    override fun test(buildTool: BuildTool) {
        when (buildTool) {
            is GradleBuildTool -> buildTool.addPlugin(junitPlatformPlugin())
            else -> throw UnsupportedBuildToolException(JavaLang, buildTool)
        }
    }
}