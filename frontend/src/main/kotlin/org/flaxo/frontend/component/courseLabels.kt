package org.flaxo.frontend.component

import org.flaxo.common.data.Course
import react.RBuilder
import react.dom.div
import react.dom.span

fun RBuilder.courseLabels(course: Course) = div {
    span(classes = "course-labels") {
        statusLabel(course)
        servicesLabels(course)
        techLabels(course)
    }
}

private fun RBuilder.servicesLabels(course: Course) =
        course.state.activatedServices
                .map { it.name }
                .map { it.toLowerCase() }
                .forEach { span(classes = "course-label badge badge-warning") { +it } }

private fun RBuilder.techLabels(course: Course) =
        setOf(course.language, course.testingLanguage, course.testingFramework)
                .map { it.toLowerCase() }
                .forEach { span(classes = "course-label badge btn-info") { +it } }

private fun RBuilder.statusLabel(course: Course) =
        span(classes = "course-label badge btn-primary") {
            +course.state.lifecycle.name.toLowerCase()
        }