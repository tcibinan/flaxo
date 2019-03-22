package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Object that is associated to some [date].
 */
interface Dated {

    /**
     * Associated date time.
     */
    val date: DateTime
}
