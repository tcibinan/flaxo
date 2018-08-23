package org.flaxo.frontend

import org.flaxo.frontend.client.FlaxoClient
import org.flaxo.frontend.client.PlainHttpFlaxoClient

object Container {
    val flaxoClient: FlaxoClient = PlainHttpFlaxoClient(Configuration.SERVER_URL)
}
