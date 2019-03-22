package org.flaxo.frontend.component

import kotlinx.html.js.onClickFunction
import org.flaxo.common.data.Course
import react.RBuilder
import react.dom.div
import react.dom.h5
import react.dom.h6
import react.dom.p
import react.dom.section
import react.dom.small

/**
 * Adds course preview card.
 */
fun RBuilder.courseCard(course: Course, onSelect: (Course) -> Unit) = section(classes = "course-item") {
    div(classes = "card") {
        attrs {
            onClickFunction = { onSelect(course) }
        }
        div(classes = "card-body") {
            h5(classes = "card-title") {
                +course.name
                courseLabels(course)
            }
            h6(classes = "card-subtitle mb-2 text-muted") {
                +"${course.tasks.size} tasks, ${course.students.size} students"
            }
            p(classes = "card-text") {
                course.description?.also { +it }
            }
            p(classes = "card-text") {
                small(classes = "text-muted") {
                    +"Created at ${course.date.toHumanReadableString()}"
                }
            }
        }
    }
}
