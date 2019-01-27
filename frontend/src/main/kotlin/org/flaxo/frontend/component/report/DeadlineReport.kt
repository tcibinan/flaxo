package org.flaxo.frontend.component.report

import org.flaxo.common.data.Solution
import org.flaxo.common.data.Task
import react.RBuilder
import react.dom.i

fun RBuilder.deadlineReport(task: Task, solution: Solution) =
        solution.commits
                .lastOrNull()
                ?.date
                ?.also { latestCommitDate ->
                    val deadline = task.deadline
                    if (deadline == null || deadline > latestCommitDate) {
                        i(classes = "material-icons valid-deadline-report") { +"alarm_on" }
                    } else {
                        i(classes = "material-icons invalid-deadline-report") { +"alarm_off" }
                    }
                }
                ?: i { }
