package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.tools.BuildTool

abstract class TestingFramework(val name: String) {
    abstract fun test(buildTool: BuildTool)
}