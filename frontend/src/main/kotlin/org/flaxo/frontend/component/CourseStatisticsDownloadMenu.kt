package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.credentials
import org.flaxo.common.Course
import org.flaxo.common.CourseLifecycle
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoHttpException
import org.w3c.dom.url.URL.Companion.createObjectURL
import org.w3c.files.Blob
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div
import kotlin.browser.document

fun RBuilder.courseStatisticsDownloadMenu(course: Course) = child(CourseStatisticsDownloadMenu::class) {
    attrs {
        this.course = course
    }
}

class CourseStatisticsDownloadMenuProps(var course: Course) : RProps

class CourseStatisticsDownloadMenu(props: CourseStatisticsDownloadMenuProps)
    : RComponent<CourseStatisticsDownloadMenuProps, EmptyState>(props) {

    private companion object {
        val DOWNLOAD_AS_DROPDOWN_ID = "downloadAsDropdown"
    }

    private val supportedFormats: List<String>

    init {
        supportedFormats = listOf("json", "csv", "tsv")
    }

    override fun RBuilder.render() {
        div("course-control") {
            div(classes = "dropdown") {
                button(classes = "btn btn-outline-secondary dropdown-toggle") {
                    attrs {
                        id = DOWNLOAD_AS_DROPDOWN_ID
                        attributes["data-toggle"] = "dropdown"
                        attributes["aria-haspopup"] = "true"
                        attributes["aria-expanded"] = "false"
                        if (props.course.state.lifecycle != CourseLifecycle.RUNNING) {
                            attributes["disabled"] = "true"
                        }
                    }
                    +"Download as"
                }
                div(classes = "dropdown-menu") {
                    attrs { attributes["aria-labelledby"] = DOWNLOAD_AS_DROPDOWN_ID }
                    supportedFormats.forEach { format ->
                        a(classes = "dropdown-item", href = "#") {
                            attrs.onClickFunction = { launch { downloadAs(format) } }
                            +format
                        }
                    }
                }
            }
        }
    }

    private suspend fun downloadAs(format: String) {
        credentials?.also {
            try {
                val data = Container.flaxoClient.downloadStatistics(it, props.course.name, format)
                val url = createObjectURL(Blob(arrayOf(data)))
                val link = document.createElement("a").asDynamic()
                link.href = url
                link.setAttribute("download", "${props.course.name}-statistics.$format")
                document.body?.appendChild(link)
                link.click()
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred while downloading ${props.course.name} course statistics.", e)
            }

        }
    }

}
