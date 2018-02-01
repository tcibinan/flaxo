package com.tcibinan.flaxo.core.env.frameworks

import com.tcibinan.flaxo.core.env.NamedEntity

abstract class TestingFramework(val name: String) : NamedEntity {
    override fun name() = name
}