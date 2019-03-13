package org.flaxo.frontend

import kotlin.browser.window

/**
 * Configuration object fields are fulfilled during the webpack bundling.
 */
object Configuration {
    val SERVER_URL: String = window.location.run { "$protocol//$hostname:8080/rest" }
    val DATA2GRAPH_URL: String = window.location.run { "$protocol//$hostname:8088" }
    const val FLAXO_VERSION: String = "{{FLAXO_VERSION}}"
}
