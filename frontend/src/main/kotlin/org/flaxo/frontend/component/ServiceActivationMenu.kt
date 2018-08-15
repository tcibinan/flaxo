package org.flaxo.frontend.component

import kotlinx.html.id
import kotlinx.html.js.onClickFunction
import org.flaxo.frontend.Container
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.credentials
import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.CourseLifecycle
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.a
import react.dom.button
import react.dom.div

fun RBuilder.serviceActivationMenu(course: Course) = child(ServiceActivationMenu::class) {
    attrs {
        this.course = course
    }
}

class ServiceActivationMenuProps(var course: Course) : RProps

class ServiceActivationMenuState(var services: Set<String>) : RState

class ServiceActivationMenu(props: ServiceActivationMenuProps)
    : RComponent<ServiceActivationMenuProps, ServiceActivationMenuState>(props) {

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
        /*
            <div className="course-control">
                <ButtonDropdown isOpen={this.state.show} toggle={this.toggle}>
                    <DropdownToggle outline caret color="primary"
                                    disabled={
                                        this.props.course.state.lifecycle !== 'RUNNING'
                                        || this.state.services
                                            .every(service =>
                                                this.props.course.state.activatedServices.includes(service)
                                            )
                                            ? 'disabled'
                                            : ''
                                    }
                    >
                        Activate service
                    </DropdownToggle>
                    <DropdownMenu>
                        <DropdownItem
                            disabled={
                                this.props
                                    .course
                                    .state
                                    .activatedServices
                                    .includes('CODACY')
                                    ? 'disabled'
                                    : ''
                            }
                            onClick={this.activateCodacy}
                        >
                            codacy
                        </DropdownItem>
                        <DropdownItem
                            disabled={
                                this.props
                                    .course
                                    .state
                                    .activatedServices
                                    .includes('TRAVIS')
                                    ? 'disabled'
                                    : ''
                            }
                            onClick={this.activateTravis}
                        >
                            travis
                        </DropdownItem>
                    </DropdownMenu>
                </ButtonDropdown>
            </div>
         */
    }

    fun activateTravis() {
        credentials?.also {
            try {
                flaxoClient.activateTravis(it, props.course.name)
                // TODO 15.08.18: notify user that travis activation has finished successfully
            } catch (e: Exception) {
                // TODO 15.08.18: notify user that travis activation has failed
            }
        }
    }

    fun activateCodacy() {
        credentials?.also {
            try {
                flaxoClient.activateCodacy(it, props.course.name)
                // TODO 15.08.18: notify user that codacy activation has finished successfully
            } catch (e: Exception) {
                // TODO 15.08.18: notify user that codacy activation has failed
            }
        }
    }

}
