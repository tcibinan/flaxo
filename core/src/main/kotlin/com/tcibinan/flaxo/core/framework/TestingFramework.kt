package com.tcibinan.flaxo.core.framework

import com.tcibinan.flaxo.core.NamedEntity

/**
 * Abstract testing framework class.
 */
abstract class TestingFramework(private val name: String) : NamedEntity {
    override fun name() = name
    override fun toString() = name()
}