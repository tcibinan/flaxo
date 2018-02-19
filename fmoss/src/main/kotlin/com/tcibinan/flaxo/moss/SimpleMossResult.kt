package com.tcibinan.flaxo.moss

import org.jsoup.Connection
import java.net.URL
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class SimpleMossResult(override val url: URL,
                       connectionSupplier: (String) -> Connection
) : MossResult {

    private var connection: Connection = connectionSupplier(url.toString())

    @Suppress("UNCHECKED_CAST")
    override fun matches(): Set<MossMatch> =
            connection.get()
                    .body()
                    .getElementsByTag("table")
                    .select("tr")
                    .drop(1)
                    .map { tr -> tr.select("td") }
                    .map { tds -> SimpleMossMatch(tds.first(), tds.second(), tds.third()) }
                    .toSet()
}

private fun Elements.second(): Element = get(1)
private fun Elements.third(): Element = get(2)
