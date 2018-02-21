package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.language.Language
import it.zielke.moji.SocketClient

class SimpleMossService(private val userId: String) : MossService {

    override fun client(language: String): Moss =
            SimpleMoss(userId, language, SocketClient())

}

