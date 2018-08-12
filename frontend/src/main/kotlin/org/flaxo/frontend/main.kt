import org.flaxo.frontend.component.rootPage
import kotlinext.js.require
import kotlin.browser.document
import react.dom.render

fun main(args: Array<String>) {
    require("bootstrap")
    require("bootstrap/dist/css/bootstrap.min.css")
    require("js-cookie")

    val documentRoot = document.getElementById("root")

    render(documentRoot) {
        rootPage()
    }
}