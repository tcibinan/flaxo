package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.LocalEnvironmentFile
import com.tcibinan.flaxo.core.env.RemoteEnvironmentFile
import it.zielke.moji.SocketClient
import org.jsoup.Jsoup

/**
 * Simple moss analysis implementation.
 */
class SimpleMoss(override val userId: String,
                 override val language: String,
                 private val client: SocketClient
) : Moss {

    private lateinit var bases: List<EnvironmentFile>
    private lateinit var solutions: List<EnvironmentFile>

    init {
        client.userID = userId
        client.language = language
    }

    override fun base(bases: List<EnvironmentFile>): Moss = apply {
        this.bases = bases
    }

    override fun solutions(solutions: List<EnvironmentFile>): Moss = apply {
        this.solutions = solutions
    }

    override fun analyse(): MossResult {
        client.run()

        bases.forEach { loadBaseFile(it) }
        solutions.forEach { loadFile(it) }

        client.sendQuery()

        return SimpleMossResult(client.resultURL, { url -> Jsoup.connect(url) })
    }

    private fun loadBaseFile(file: EnvironmentFile) = loadFile(file, isBase = true)

    private fun loadFile(file: EnvironmentFile, isBase: Boolean = false) {
        when (file) {
            is LocalEnvironmentFile -> client.uploadFile(file.file(), isBase)
            is RemoteEnvironmentFile -> client.uploadFile(file.file(), isBase)
            else -> throw Exception("Unsupported environment file type ${this::class} to be used with moss.")
        }
    }

}

