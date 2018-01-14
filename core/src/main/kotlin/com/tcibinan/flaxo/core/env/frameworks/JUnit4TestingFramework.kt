package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin

object JUnit4TestingFramework : TestingFramework("junit4") {
    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
    }
}