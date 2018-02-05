package com.tcibinan.flaxo.core.framework

import com.tcibinan.flaxo.core.NamedEntity

abstract class TestingFramework(val name: String) : NamedEntity {
    override fun name() = name

    override fun toString() = name()
}