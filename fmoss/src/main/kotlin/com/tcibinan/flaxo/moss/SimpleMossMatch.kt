package com.tcibinan.flaxo.moss

import org.jsoup.nodes.Element

class SimpleMossMatch(file1: Element,
                      file2: Element,
                      matchedLinesCount: Element
): MossMatch {
    override fun students(): Pair<String, String> {
        TODO("not implemented")
    }

    override fun lines(): Int {
        TODO("not implemented")
    }

    override fun percentage(): Int {
        TODO("not implemented")
    }

}