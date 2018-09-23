package org.flaxo.frontend.component

import kotlinx.coroutines.experimental.launch
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.common.Course
import org.flaxo.common.CourseLifecycle
import org.flaxo.common.ExternalService
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoHttpException
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
    private val availableValidations: Set<ExternalService>

    init {
        flaxoClient = Container.flaxoClient
        availableValidations = setOf(ExternalService.TRAVIS, ExternalService.CODACY)
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
                                || props.course.state.activatedServices.all { it in availableValidations }
                    }
                    +"Activate service"
                }
                div(classes = "dropdown-menu") {
                    attrs { attributes["aria-labelledby"] = SERVICE_ACTIVATION_DROPDOWN_ID }
                    a(classes = "dropdown-item", href = "#") {
                        attrs {
                            when (ExternalService.TRAVIS) {
                                in props.course.state.activatedServices -> classes = setOf("dropdown-item", "disabled")
                                else -> {
                                    classes = setOf("dropdown-item")
                                    onClickFunction = { launch { activateTravis() } }
                                }
                            }
                        }
                        +"Travis"
                    }
                    a(classes = "dropdown-item", href = "#") {
                        attrs {
                            when (ExternalService.CODACY) {
                                in props.course.state.activatedServices -> classes = setOf("dropdown-item", "disabled")
                                else -> {
                                    classes = setOf("dropdown-item")
                                    onClickFunction = { launch { activateCodacy() } }
                                }
                            }
                        }
                        +"Codacy"
                    }
                }
            }
        }
    }

    private suspend fun activateTravis() {
        credentials?.also {
            try {
                Notifications.info("Travis activation has been started.")
                flaxoClient.activateTravis(it, props.course.name)
                Notifications.success("Travis activation has been finished.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred during travis activation.", e)
            }
        }
    }

    private suspend fun activateCodacy() {
        credentials?.also {
            try {
                Notifications.info("Codacy activation has been started.")
                flaxoClient.activateCodacy(it, props.course.name)
                Notifications.success("Codacy activation has been finished.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred during codacy activation.", e)
            }
        }
    }

}
