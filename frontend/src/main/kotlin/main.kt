import components.rootPage
import kotlinext.js.require
import kotlin.browser.document
import react.dom.render

val documentRoot = document.getElementById("root")

fun main(args: Array<String>) {
    require("bootstrap")
    require("bootstrap/dist/css/bootstrap.min.css")
    require("js-cookie")

    render(documentRoot) {
        rootPage()
    }
}