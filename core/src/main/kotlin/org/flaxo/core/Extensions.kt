package org.flaxo.core

import java.util.concurrent.TimeUnit

/**
 * Converts [this] of [units] to millis.
 */
infix fun Number.of(units: TimeUnit): Long =
    TimeUnit.MILLISECONDS.convert(this.toLong(), units)
