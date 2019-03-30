package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.Dated
import org.flaxo.common.Identifiable

/**
 * Solution build report.
 */
@Serializable
data class BuildReport(

        override val id: Long,

        override val date: DateTime,

        /**
         * Specifies if the build report was succeed.
         */
        val succeed: Boolean
): Identifiable, Dated
