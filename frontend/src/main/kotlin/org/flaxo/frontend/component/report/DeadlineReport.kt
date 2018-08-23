package org.flaxo.frontend.component.report

import org.flaxo.frontend.data.Solution
import org.flaxo.frontend.data.Task
import react.RBuilder
import react.dom.div
import react.dom.i
import kotlin.js.Date

fun RBuilder.deadlineReport(task: Task, solution: Solution) =
        solution.commits
                .lastOrNull()
                ?.date
                ?.also { latestCommitDate ->
                    if (task.deadline == null || task.deadline > latestCommitDate) {
                        i(classes = "material-icons valid-deadline-report") { +"alarm_on" }
                    } else {
                        i(classes = "material-icons invalid-deadline-report") { +"alarm_off" }
                    }
                }
                ?: i { }

operator fun Date.compareTo(anotherDate: Date): Int = this.getTime().compareTo(anotherDate.getTime())
