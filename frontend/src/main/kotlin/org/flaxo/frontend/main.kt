import org.flaxo.frontend.component.page
import kotlinext.js.require
import org.flaxo.frontend.wrapper.NotificationContainer
import kotlin.browser.document
import react.dom.render

fun main(args: Array<String>) {
    require("bootstrap")
    require("bootstrap/dist/css/bootstrap.min.css")
    require("js-cookie")
    require("react-notifications")
    require("react-notifications/lib/notifications.css")

    val documentRoot = document.getElementById("root")

    render(documentRoot) {
        page()
        NotificationContainer { }
    }
}