package org.flaxo.frontend

/**
 * Configuration object fields are fulfilled during the webpack bundling.
 */
object Configuration {
    const val SERVER_URL = "{{REST_URL}}"
    const val FLAXO_VERSION = "{{FLAXO_VERSION}}"
}
