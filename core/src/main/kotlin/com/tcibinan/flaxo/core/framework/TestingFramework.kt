package com.tcibinan.flaxo.core.framework

import com.tcibinan.flaxo.core.NamedEntity

/**
 * Abstract testing framework class.
 */
abstract class TestingFramework(override val name: String)
    : NamedEntity {

    override fun toString() = name
}