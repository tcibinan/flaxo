package org.flaxo.model.data

import java.time.LocalDateTime

/**
 * Report entity interface.
 */
interface Report {

    /**
     * Report date.
     */
    val date: LocalDateTime
}