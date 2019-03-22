package org.flaxo.model.data

import java.time.LocalDateTime

/**
 * Report entity interface.
 */
@Deprecated("Should be replace with Dated from the common module", replaceWith = ReplaceWith("Dated", "org.flaxo.common.data.Dated"))
interface Report {

    /**
     * Report date.
     */
    val date: LocalDateTime
}
