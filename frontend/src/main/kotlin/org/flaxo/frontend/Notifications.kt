package org.flaxo.frontend

import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.wrapper.ReactNotificationManager

// Hour in milliseconds
const val HOUR = 60 * 60 * 1000

class Notifications {
    companion object {

        /**
         * Popups an info notification with the specified [title] and [message].
         */
        fun info(message: String, title: String? = null) =
                ReactNotificationManager.info(message, title, time = HOUR)

        /**
         * Popups a success notification with the specified [title] and [message].
         */
        fun success(message: String, title: String? = null) =
                ReactNotificationManager.success(message, title, time = HOUR)

        /**
         * Popups a warning notification with the specified [title] and [message].
         */
        fun warning(message: String, title: String? = null) =
                ReactNotificationManager.warning(message, title, time = HOUR)

        /**
         * Popups an error notification with the specified [title] and [message].
         */
        fun error(message: String, title: String? = null) =
                ReactNotificationManager.error(message, title, time = HOUR)

        /**
         * Popups an error notification with the specified [message] and error message retrieved from [exception].
         */
        fun error(message: String, exception: FlaxoHttpException) =
                if (exception.userMessage == null) error(message)
                else error(message + "\n\n" + exception.userMessage)
    }
}
