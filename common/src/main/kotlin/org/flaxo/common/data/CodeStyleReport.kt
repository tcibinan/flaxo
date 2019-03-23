package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.DateTime

/**
 * Solution code style report.
 */
@Serializable
data class CodeStyleReport(

        override val id: Long,

        override val date: DateTime,

        /**
         * Code style report grade.
         */
        val grade: CodeStyleGrade
) : Identifiable, Dated
