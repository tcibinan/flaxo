package org.flaxo.moss

import it.zielke.moji.SocketClient
import org.flaxo.common.Language
import org.flaxo.common.env.file.LocalFile
import java.net.URL

/**
 * Simple Moss analysis implementation.
 */
class SimpleMoss(private val client: SocketClient) : Moss {

    companion object {
        fun using(client: SocketClient): Moss = SimpleMoss(client)
        fun of(userId: String, language: Language): Moss = using(SocketClient().apply {
            this.userID = userId
            this.language = when (language) {
                Language.C -> "c"
                Language.Cpp -> "cc"
                Language.Java -> "java"
                Language.Pascal -> "pascal"
                Language.Lisp -> "lisp"
                Language.Haskell -> "haskell"
                Language.Fortran -> "fortran"
                Language.Perl -> "perl"
                Language.Matlab -> "matlab"
                Language.Python -> "python"
                Language.Prolog -> "prolog"
                Language.Javascript -> "javascript"
                Language.PlSql -> "plsql"
                else ->
                    if (language.alias in supportedLanguages) language.alias
                    else throw MossException("Language ${language.alias} is not supported by MOSS.")
            }
        })
    }

    override fun submit(submission: MossSubmission): URL {
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

        return client.resultURL
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
