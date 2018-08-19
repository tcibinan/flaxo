package org.flaxo.frontend.component

import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.CourseLifecycle
import org.flaxo.frontend.wrapper.NotificationManager
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div


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

    private val flaxoClient: FlaxoClient

    init {
        flaxoClient = Container.flaxoClient
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
                    a(classes = "dropdown-item", href = "#") {
                        attrs { onClickFunction = { downloadAs("json") } }
                        +"json"
                    }
                    a(classes = "dropdown-item", href = "#") {
                        attrs { onClickFunction = { downloadAs("csv") } }
                        +"csv"
                    }
                    a(classes = "dropdown-item disabled", href = "#") {
                        attrs { onClickFunction = { downloadAs("xls") } }
                        +"xls"
                    }
                }
            }
        }
    }

    private fun downloadAs(format: String) {
        credentials?.also {
            try {
                // TODO 19.08.18: Implement statistics downloading
                /*
                val data = flaxoClient.downloadStatistics(it, props.course.name, format)

                TODO 15.08.18: Transform from js code:
                const url = window.URL.createObjectURL(new Blob([data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', `${this.props.course.name}-statistics.${format}`);
                document.body.appendChild(link);
                link.click();
                 */
            } catch (e: Exception) {
                console.log(e)
                NotificationManager.error("Error occurred while downloading ${props.course.name} course statistics.")
            }

        }
    }

}
