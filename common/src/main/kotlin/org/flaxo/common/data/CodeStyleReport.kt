package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Solution code style report.
 */
data class CodeStyleReport(

        override val id: Long,

        override val date: DateTime,

        /**
         * Code style report grade.
         */
        val grade: CodeStyleGrade
) : Identifiable, Dated
