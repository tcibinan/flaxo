package com.tcibinan.flaxo.gradle

import java.io.File

fun File.fillWith(content: String) =
        outputStream().bufferedWriter()
                .use { it.write(content) }