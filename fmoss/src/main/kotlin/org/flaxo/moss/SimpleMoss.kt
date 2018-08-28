package org.flaxo.moss

import it.zielke.moji.SocketClient
import org.flaxo.core.env.file.LocalFile
import org.jsoup.Jsoup

/**
 * Simple moss analysis implementation.
 */
class SimpleMoss(override val userId: String,
                 override val language: String,
                 private val client: SocketClient,
                 private val bases: List<LocalFile> = emptyList(),
                 private val solutions: List<LocalFile> = emptyList()
) : Moss {

    init {
        client.userID = userId
        client.language = language
    }

    override fun base(bases: List<LocalFile>): Moss =
            SimpleMoss(userId, language, client, bases, solutions)

    override fun solutions(solutions: List<LocalFile>): Moss =
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

    private fun loadBaseFile(file: LocalFile): Unit = loadFile(file, isBase = true)

    private fun loadFile(file: LocalFile, isBase: Boolean = false): Unit =
            try {
                client.uploadFile(file.localPath.toFile(), isBase)
            } catch (e: Exception) {
                throw MossException("Can't load ${if (isBase) "base" else "solutions"} " +
                        "file ${file.fileName} to moss server", e)
            }

}

