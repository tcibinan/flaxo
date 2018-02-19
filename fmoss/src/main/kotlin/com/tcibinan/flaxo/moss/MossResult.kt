package com.tcibinan.flaxo.moss

import java.net.URL

interface MossResult {
    val url: URL
    fun matches(): Set<MossMatch>
}
