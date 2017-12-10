package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.frameworks.JUnit4TestingFramework
import com.tcibinan.flaxo.core.env.frameworks.SpekTestingFramework
import com.tcibinan.flaxo.core.env.tools.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.kotlinGradlePlugin
import com.tcibinan.flaxo.core.env.tools.kotlinJreDependency
import com.tcibinan.flaxo.core.env.tools.kotlinTest

object KotlinLang : AbstractLanguage("kotlin",
        suitableTestLanguages = setOf(),
        suitableTestingFrameworks = setOf(JUnit4TestingFramework, SpekTestingFramework)
) {

    override fun main(buildTool: BuildTool) {
        buildTool
                .addPlugin(kotlinGradlePlugin())
                .addDependency(kotlinJreDependency())
    }

    override fun test(buildTool: BuildTool) {
        buildTool
                .addPlugin(junitPlatformPlugin())
                .addDependency(kotlinTest())
    }
}