package org.flaxo.core

import java.util.concurrent.TimeUnit

/**
 * Repeats the given [action] at most [attemptsLimit] times.
 *
 * Before calling [action] the first time sleeps for [initDelay] seconds.
 * After each call of [action] sleeps for [retrievingDelay] seconds.
 *
 * @throws FlaxoException If action doesn't return true after [attemptsLimit] calls.
 */
fun repeatUntil(actionTitle: String,
                attemptsLimit: Int = 20,
                retrievingDelay: Long = 3,
                initDelay: Long = 3,
                observationDuration: Long = (attemptsLimit) * retrievingDelay,
                action: () -> Boolean
) {
    initDelay.takeIf { it > 0 }
            ?.also { Thread.sleep(it of TimeUnit.SECONDS) }

    val messages = mutableListOf<String>()

    repeat(attemptsLimit) {
        try {
            if (action()) return
        } catch (e: Throwable) {
            messages.add(e.simplifiedStringStackTrace())
        }
        Thread.sleep(retrievingDelay of TimeUnit.SECONDS)
    }

    throw FlaxoException("$actionTitle hasn't finished after $observationDuration seconds. " +
            "All ${messages.size} catched exception listed below: ${messages.joinToString("\n", "\n")}")
}