package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.tools.junitPlatformPlugin
import com.tcibinan.flaxo.core.env.tools.spekApi
import com.tcibinan.flaxo.core.env.tools.spekDataDriven
import com.tcibinan.flaxo.core.env.tools.spekJunitRunner
import com.tcibinan.flaxo.core.env.tools.spekSubject

object SpekTestingFramework : TestingFramework("spek") {
    override fun test(buildTool: BuildTool) {
        buildTool.addPlugin(junitPlatformPlugin())
                .addDependency(spekApi())
                .addDependency(spekDataDriven())
                .addDependency(spekSubject())
                .addDependency(spekJunitRunner())
    }
}