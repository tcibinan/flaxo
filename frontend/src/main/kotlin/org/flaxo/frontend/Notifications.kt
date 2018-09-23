package org.flaxo.frontend

import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.wrapper.ReactNotificationManager

// Hour in milliseconds
const val HOUR = 60 * 60 * 1000

class Notifications {
    companion object {
        fun info(message: String, title: String? = null) =
                ReactNotificationManager.info(message, title, time = HOUR)

        fun success(message: String, title: String? = null) =
                ReactNotificationManager.success(message, title, time = HOUR)

        fun warning(message: String, title: String? = null) =
                ReactNotificationManager.warning(message, title, time = HOUR)

        fun error(message: String, title: String? = null) =
                ReactNotificationManager.error(message, title, time = HOUR)

        fun error(message: String, e: FlaxoHttpException) =
                if (e.userMessage == null) error(message)
                else error(message + "\n\n" + e.userMessage)
    }
}
