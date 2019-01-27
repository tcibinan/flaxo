package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Solution code style report.
 */
class CodeStyleReport(
        /**
         * Code style report grade.
         */
        val grade: CodeStyleGrade,

        /**
         * Code style report creation date time.
         */
        val date: DateTime
)