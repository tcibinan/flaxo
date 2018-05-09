package org.flaxo.core

import java.util.concurrent.TimeUnit

fun repeatUntil(actionTitle: String,
                attemptsLimit: Int = 20,
                retrievingDelay: Long = 3,
                observationDuration: Long = (attemptsLimit) * retrievingDelay,
                action: () -> Boolean
) {
    repeat(attemptsLimit) {
        Thread.sleep(retrievingDelay of TimeUnit.SECONDS)
        if (action()) return
    }

    throw FlaxoException("$actionTitle hasn't finished after $observationDuration seconds.")
}