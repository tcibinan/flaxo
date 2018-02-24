package com.tcibinan.flaxo.rest.service.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile

data class MossTask(val taskName: String,
                    val base: List<EnvironmentFile>,
                    val solutions: List<EnvironmentFile>)