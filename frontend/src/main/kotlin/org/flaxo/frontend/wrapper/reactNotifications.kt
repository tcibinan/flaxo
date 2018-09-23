@file:JsModule("react-notifications")

package org.flaxo.frontend.wrapper

import react.RClass

@JsName("NotificationManager")
external class ReactNotificationManager {
    companion object {
        fun info(message: String,
                 title: String? = definedExternally,
                 time: Int? = definedExternally,
                 callback: (() -> Unit)? = definedExternally)

        fun success(message: String,
                    title: String? = definedExternally,
                    time: Int? = definedExternally,
                    callback: (() -> Unit)? = definedExternally)

        fun warning(message: String,
                    title: String? = definedExternally,
                    time: Int? = definedExternally,
                    callback: (() -> Unit)? = definedExternally)

        fun error(message: String,
                  title: String? = definedExternally,
                  time: Int? = definedExternally,
                  callback: (() -> Unit)? = definedExternally)
    }
}

@JsName("NotificationContainer")
external val NotificationContainer: RClass<dynamic>
