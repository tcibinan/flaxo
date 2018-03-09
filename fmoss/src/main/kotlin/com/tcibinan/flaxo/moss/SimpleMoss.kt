package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
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
        client.run()

        bases.forEach { loadBaseFile(it) }
        solutions.forEach { loadFile(it) }

        client.sendQuery()

        return SimpleMossResult(client.resultURL, { url -> Jsoup.connect(url) })
    }

    private fun loadBaseFile(file: EnvironmentFile) = loadFile(file, isBase = true)

    private fun loadFile(file: EnvironmentFile, isBase: Boolean = false) =
            try {
                client.uploadFile(file.file(), isBase)
            } catch (e: Throwable) {
                throw MossException("Unsupported environment file type ${this::class} " +
                        "to be used with moss.", e)
            }

}

