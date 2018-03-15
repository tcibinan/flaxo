package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
import io.vavr.kotlin.Try
import it.zielke.moji.SocketClient
import org.jsoup.Jsoup

/**
 * Simple moss analysis implementation.
 */
class SimpleMoss(override val userId: String,
                 override val language: String,
                 private val client: SocketClient,
                 private val bases: List<EnvironmentFile> = emptyList(),
                 private val solutions: List<EnvironmentFile> = emptyList()
) : Moss {

    init {
        client.userID = userId
        client.language = language
    }

    override fun base(bases: List<EnvironmentFile>): Moss =
            SimpleMoss(userId, language, client, bases, solutions)

    override fun solutions(solutions: List<EnvironmentFile>): Moss =
            SimpleMoss(userId, language, client, bases, solutions)

    override fun analyse(): MossResult {
        Try {
            client.run()

            bases.forEach { loadBaseFile(it) }
            solutions.forEach { loadFile(it) }

            client.sendQuery()
        }.andFinally {
            // Moji's socket client does not implement AutoCloseable
            client.close()
        }

        return SimpleMossResult(client.resultURL, { url -> Jsoup.connect(url) })
    }

    private fun loadBaseFile(file: EnvironmentFile) = loadFile(file, isBase = true)

    private fun loadFile(environmentFile: EnvironmentFile, isBase: Boolean = false) =
            environmentFile.use {
                Try {
                    client.uploadFile(it.file(), isBase)
                }.onFailure { e ->
                    throw MossException("Can't load ${if (isBase) "base" else "solutions"} " +
                            "file ${environmentFile.name()} to moss server", e)
                }
            }

}

