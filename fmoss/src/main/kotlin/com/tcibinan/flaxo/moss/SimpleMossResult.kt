package com.tcibinan.flaxo.moss

import org.jsoup.Connection
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URL

/**
 * Moss result implementation.
 */
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
                    .map { tds ->
                        val first = tds.first().selectFirst("a")
                        val second = tds.second().selectFirst("a")

                        val link = first.attr("href")

                        val firstPath = first.text().split(" ").first().split("/")
                        val secondPath = second.text().split(" ").first().split("/")

                        val students =
                                firstPath.zip(secondPath)
                                        .dropWhile { it.first == it.second }
                                        .first()

                        val lines = tds.third().text().toInt()

                        val percentage = first.text().split(" ")
                                .last()
                                .removeSurrounding("(", "%)")
                                .toInt()

                        MossMatch(students, lines, link, percentage)
                    }
                    .toSet()
}

private fun Elements.second(): Element = get(1)
private fun Elements.third(): Element = get(2)
