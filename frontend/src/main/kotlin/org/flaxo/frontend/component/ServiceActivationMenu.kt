package org.flaxo.frontend.component

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.common.data.ExternalService
import org.flaxo.frontend.Container
import org.flaxo.frontend.Notifications
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.FlaxoHttpException
import org.flaxo.frontend.credentials
import react.RBuilder
import react.RComponent
import react.RProps
import react.dom.a
import react.dom.button
import react.dom.div

/**
 * Adds course services activation menu.
 */
fun RBuilder.serviceActivationMenu(course: Course) = child(ServiceActivationMenu::class) {
    attrs {
        this.course = course
    }
}

private class ServiceActivationMenuProps(var course: Course) : RProps

private class ServiceActivationMenu(props: ServiceActivationMenuProps)
    : RComponent<ServiceActivationMenuProps, EmptyState>(props) {

    private companion object {
        val SERVICE_ACTIVATION_DROPDOWN_ID = "serviceActivationDropdown"
    }

    private val flaxoClient: FlaxoClient
    private val availableValidations: Set<ExternalService>

    init {
        flaxoClient = Container.flaxoClient
        availableValidations = ExternalService.values().toSet() - ExternalService.GITHUB
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
                                || availableValidations.all { it in props.course.state.activatedServices }
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
                                    onClickFunction = { GlobalScope.launch { activateTravis() } }
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
                                    onClickFunction = { GlobalScope.launch { activateCodacy() } }
                                }
                            }
                        }
                        +"Codacy"
                    }
                    a(classes = "dropdown-item", href = "#") {
                        attrs {
                            when (ExternalService.GITPLAG) {
                                in props.course.state.activatedServices -> classes = setOf("dropdown-item", "disabled")
                                else -> {
                                    classes = setOf("dropdown-item")
                                    onClickFunction = { GlobalScope.launch { activateGitplag() } }
                                }
                            }
                        }
                        +"Gitplag"
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

    private suspend fun activateGitplag() {
        credentials?.also {
            try {
                Notifications.info("Gitplag activation has been started.")
                flaxoClient.activateGitplag(it, props.course.name)
                Notifications.success("Gitplag activation has been finished.")
            } catch (e: FlaxoHttpException) {
                console.log(e)
                Notifications.error("Error occurred during gitplag activation.", e)
            }
        }
    }

}
