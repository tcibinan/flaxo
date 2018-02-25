package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.GitPayload
import java.io.Reader

interface GitService {
    fun with(credentials: String): Git
    fun parsePayload(reader: Reader, headers: Map<String, List<String>>): GitPayload?
}