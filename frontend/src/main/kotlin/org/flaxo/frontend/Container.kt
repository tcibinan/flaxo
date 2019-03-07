package org.flaxo.frontend

import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.XMLHttpRequestFlaxoClient

/**
 * Services container.
 *
 * In other words it is a primitive dependency injection container.
 */
object Container {
    val flaxoClient: FlaxoClient = XMLHttpRequestFlaxoClient(Configuration.SERVER_URL)
}
