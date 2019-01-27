package org.flaxo.frontend.component.report

import org.flaxo.common.data.Solution
import react.RBuilder
import react.dom.div

fun RBuilder.codeStyleReport(solution: Solution) =
        solution.codeStyleReports
                .lastOrNull()
                ?.also { div(classes = "code-style-grade code-style-grade-" + it.grade) { } }
                ?: div { }
