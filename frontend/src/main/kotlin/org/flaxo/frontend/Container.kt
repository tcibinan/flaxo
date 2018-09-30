package org.flaxo.frontend

import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.XMLHttpRequestFlaxoClient

object Container {
    val flaxoClient: FlaxoClient = XMLHttpRequestFlaxoClient(Configuration.SERVER_URL)
}
