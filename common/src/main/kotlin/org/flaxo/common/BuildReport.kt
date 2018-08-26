package org.flaxo.common

/**
 * Solution build report.
 */
class BuildReport(
        /**
         * Specifies if the build report was succeed.
         */
        val succeed: Boolean,

        /**
         * Build report creation date time.
         */
        val date: DateTime
)