package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.NamedEntity
import com.tcibinan.flaxo.core.env.tools.BuildTool

abstract class TestingFramework(val name: String) : NamedEntity {
    override fun name() = name
    abstract fun test(buildTool: BuildTool)
}