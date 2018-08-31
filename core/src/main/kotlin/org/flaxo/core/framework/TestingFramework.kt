package org.flaxo.core.framework

import org.flaxo.core.NamedEntity

/**
 * Abstract testing framework.
 */
abstract class TestingFramework(override val name: String) : NamedEntity {

    override fun toString() = name
}
