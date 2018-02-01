package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.gradle.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.gradle.spekApiDependency
import com.tcibinan.flaxo.core.env.tools.gradle.spekDataDrivenDependency
import com.tcibinan.flaxo.core.env.tools.gradle.spekJunitRunnerDependency
import com.tcibinan.flaxo.core.env.tools.gradle.spekSubjectDependency

object SpekTestingFramework : TestingFramework("spek") {
    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
                .addDependency(spekApiDependency())
                .addDependency(spekDataDrivenDependency())
                .addDependency(spekSubjectDependency())
                .addDependency(spekJunitRunnerDependency())
    }
}