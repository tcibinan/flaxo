package org.flaxo.rest.service.git

import org.flaxo.git.Git
import org.flaxo.git.GitPayload
import java.io.Reader

interface GitService {
    fun with(credentials: String): Git
    fun parsePayload(reader: Reader, headers: Map<String, List<String>>): GitPayload?
}