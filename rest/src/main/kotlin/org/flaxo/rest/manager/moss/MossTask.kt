package org.flaxo.rest.manager.moss

import org.flaxo.core.env.EnvironmentFile

/**
 * Moss analysis task parameters.
 */
data class MossTask(val taskName: String,
                    val base: List<EnvironmentFile>,
                    val solutions: List<EnvironmentFile>
)