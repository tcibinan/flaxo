package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.UnsupportedBuildToolException
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.env.tools.GradleBuildTool
import com.tcibinan.flaxo.core.env.tools.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.kotlinGradlePlugin
import com.tcibinan.flaxo.core.env.tools.kotlinJreDependency
import com.tcibinan.flaxo.core.env.tools.kotlinTest

object KotlinLang : Language("kotlin",
        suitableTestLanguages = setOf(KotlinLang),
        suitableTestingFrameworks = setOf(JUnit4TestingFramework, SpekTestingFramework)
) {

    override fun main(buildTool: BuildTool) {
        when (buildTool) {
            is GradleBuildTool -> {
                buildTool
                        .addPlugin(kotlinGradlePlugin())
                        .addDependency(kotlinJreDependency())

            }
            else -> throw UnsupportedBuildToolException(KotlinLang, buildTool)
        }
    }

    override fun test(buildTool: BuildTool) {
        when (buildTool) {
            is GradleBuildTool -> {
                buildTool
                        .addPlugin(junitPlatformPlugin())
                        .addDependency(kotlinTest())
            }
        }
    }
}