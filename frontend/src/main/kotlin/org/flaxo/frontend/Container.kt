package org.flaxo.frontend

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.XMLHttpRequestFlaxoClient

/**
 * Services container.
 *
 * In other words it is a primitive dependency injection container.
 */
object Container {
    val json: Json = Json(JsonConfiguration.Stable)
    val flaxoClient: FlaxoClient = XMLHttpRequestFlaxoClient(Configuration.SERVER_URL)
}
