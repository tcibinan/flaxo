package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.gradle.spekApi
import com.tcibinan.flaxo.core.env.tools.gradle.spekDataDriven
import com.tcibinan.flaxo.core.env.tools.gradle.spekJunitRunner
import com.tcibinan.flaxo.core.env.tools.gradle.spekSubject

object SpekTestingFramework : TestingFramework("spek") {
    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
                .addDependency(spekApi())
                .addDependency(spekDataDriven())
                .addDependency(spekSubject())
                .addDependency(spekJunitRunner())
    }
}