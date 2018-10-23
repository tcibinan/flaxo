package org.flaxo.moss

import it.zielke.moji.SocketClient
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.CppLang
import org.flaxo.core.lang.Language
import org.jsoup.Jsoup

/**
 * Simple moss analysis implementation.
 */
class SimpleMoss(private val client: SocketClient) : Moss {

    companion object {
        fun using(client: SocketClient): Moss = SimpleMoss(client)
        fun of(userId: String, language: Language): Moss = using(SocketClient().apply {
            this.userID = userId
            this.language = when(language) {
                CppLang -> "cc"
                else -> language.name
            }
        })
    }

    override fun analyse(submission: MossSubmission): MossResult {
        try {
            client.run()

            with(submission) {
                base.forEach { loadBaseFile(it) }
                solutions.forEach { loadFile(it) }
            }

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
