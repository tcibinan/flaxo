package com.tcibinan.flaxo.rest.service.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile

data class MossTask(val base: Set<EnvironmentFile>,
               val solutions: Set<EnvironmentFile>)