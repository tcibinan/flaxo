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

fun RBuilder.serviceActivationMenu(course: Course) = child(ServiceActivationMenu::class) {
    attrs {
        this.course = course
    }
}

class ServiceActivationMenuProps(var course: Course) : RProps
class ServiceActivationMenu(props: ServiceActivationMenuProps)
    : RComponent<ServiceActivationMenuProps, EmptyState>(props) {

    private companion object {
        val SERVICE_ACTIVATION_DROPDOWN_ID = "serviceActivationDropdown"
    }

    private val flaxoClient: FlaxoClient
    private val integratedServices: Set<String>

    init {
        flaxoClient = Container.flaxoClient
        integratedServices = setOf("TRAVIS", "CODACY")
    }

    override fun RBuilder.render() {
        div("course-control") {
            div(classes = "dropdown") {
                button(classes = "btn btn-outline-primary dropdown-toggle") {
                    attrs {
                        id = SERVICE_ACTIVATION_DROPDOWN_ID
                        attributes["data-toggle"] = "dropdown"
                        attributes["aria-haspopup"] = "true"
                        attributes["aria-expanded"] = "false"
                        disabled = props.course.state.lifecycle != CourseLifecycle.RUNNING
                                || props.course.state.activatedServices.all { it in integratedServices }
                    }
                    +"Activate service"
                }
                div(classes = "dropdown-menu") {
                    attrs { attributes["aria-labelledby"] = SERVICE_ACTIVATION_DROPDOWN_ID }
                    a(classes = "dropdown-item") {
                        attrs {
                            onClickFunction = { activateTravis() }
                            attributes["disabled"] = ("TRAVIS" in props.course.state.activatedServices).toString()
                        }
                        +"Travis"
                    }
                    a(classes = "dropdown-item") {
                        attrs {
                            onClickFunction = { activateCodacy() }
                            attributes["disabled"] = ("CODACY" in props.course.state.activatedServices).toString()
                        }
                        +"Codacy"
                    }
                }
            }
        }
    }

    private fun activateTravis() {
        credentials?.also {
            try {
                flaxoClient.activateTravis(it, props.course.name)
                NotificationManager.success("Travis activation has been finished.")
            } catch (e: Exception) {
                console.log(e)
                NotificationManager.success("Error occurred during travis activation.")
            }
        }
    }

    private fun activateCodacy() {
        credentials?.also {
            try {
                flaxoClient.activateCodacy(it, props.course.name)
                NotificationManager.success("Codacy activation has been finished.")
            } catch (e: Exception) {
                console.log(e)
                NotificationManager.success("Error occurred during codacy activation.")
            }
        }
    }

}
