package org.flaxo.common

import org.flaxo.common.data.DateTime

/**
 * Object that is associated to some [date].
 */
interface Dated {

    /**
     * Associated date time.
     */
    val date: DateTime
}
