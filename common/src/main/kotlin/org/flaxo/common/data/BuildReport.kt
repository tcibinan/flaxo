package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Solution build report.
 */
data class BuildReport(

        override val id: Long,

        override val date: DateTime,

        /**
         * Specifies if the build report was succeed.
         */
        val succeed: Boolean
): Identifiable, Dated
