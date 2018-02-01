package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.gradle.jupiterApiDependency
import com.tcibinan.flaxo.core.env.tools.gradle.jupiterEngineDependency

object JUnitTestingFramework : TestingFramework("junit") {
    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
                .addDependency(jupiterApiDependency())
                .addDependency(jupiterEngineDependency())
    }
}