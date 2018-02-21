package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.env.LocalEnvironmentFile
import com.tcibinan.flaxo.core.language.Language
import it.zielke.moji.SocketClient
import org.jsoup.Jsoup

class SimpleMoss(override val userId: String,
                 override val language: String,
                 private val client: SocketClient
) : Moss {

    private lateinit var bases: Set<EnvironmentFile>
    private lateinit var solutions: Set<EnvironmentFile>

    init {
        client.userID = userId
        client.language = language
    }

    override fun base(bases: Set<EnvironmentFile>): Moss = apply {
        this.bases = bases
    }

    override fun solutions(solutions: Set<EnvironmentFile>): Moss = apply {
        this.solutions = solutions
    }

    override fun analyse(): MossResult {
        val tempDir = createTempDir("moss-analyse")
        tempDir.deleteOnExit()

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
            else -> TODO("Add support for in-memory uploading")
        }
    }

}

