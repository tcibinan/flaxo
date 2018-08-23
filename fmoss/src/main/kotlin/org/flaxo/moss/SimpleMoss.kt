package org.flaxo.moss

import org.flaxo.core.env.EnvironmentFile
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
        try {
            client.run()

            bases.forEach { loadBaseFile(it) }
            solutions.forEach { loadFile(it) }

            client.sendQuery()
        } finally {
            // Moji's socket client does not implement AutoCloseable
            client.close()
        }

        return SimpleMossResult(client.resultURL) { url -> Jsoup.connect(url) }
    }

    private fun loadBaseFile(file: EnvironmentFile) = loadFile(file, isBase = true)

    private fun loadFile(environmentFile: EnvironmentFile,
                         isBase: Boolean = false) =
            environmentFile.use {
                try {
                    client.uploadFile(it.file, isBase)
                } catch (e: Exception) {
                    throw MossException("Can't load ${if (isBase) "base" else "solutions"} " +
                            "file ${it.fileName} to moss server", e)
                }
            }

}

