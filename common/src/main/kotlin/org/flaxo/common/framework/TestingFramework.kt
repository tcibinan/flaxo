package org.flaxo.common.framework

import org.flaxo.common.NamedEntity

/**
 * Abstract testing framework.
 */
abstract class TestingFramework(override val name: String) : NamedEntity {

    override fun toString() = name
}
