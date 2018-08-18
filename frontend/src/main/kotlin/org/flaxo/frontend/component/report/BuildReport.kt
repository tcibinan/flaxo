package org.flaxo.frontend.component.report

import org.flaxo.frontend.data.Solution
import react.RBuilder
import react.dom.i

fun RBuilder.buildReport(solution: Solution) = solution.buildReports
        .lastOrNull()
        ?.also {
            if (it.succeed) {
                i(classes = "material-icons successful-build-report") { +"done" }
            } else {
                i(classes = "material-icons failed-build-report") { +"clear" }
            }
        }
        ?: i(classes = "material-icons") { }