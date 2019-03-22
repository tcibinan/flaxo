package org.flaxo.common.framework

import org.flaxo.common.data.Named

/**
 * Abstract testing framework.
 */
abstract class TestingFramework(override val name: String) : Named {

    override fun toString() = name
}
