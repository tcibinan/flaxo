package org.flaxo.common

import org.flaxo.common.data.Named

/**
 * Named entity interface.
 */
@Deprecated("Should be replace with just a Named interface", ReplaceWith("Named", "org.flaxo.common.data.Named"))
interface NamedEntity: Named
