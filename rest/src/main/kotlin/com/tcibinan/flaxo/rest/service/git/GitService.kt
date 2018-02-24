package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.Git
import com.tcibinan.flaxo.git.GitPayload
import javax.servlet.http.HttpServletRequest

interface GitService {
    fun with(credentials: String): Git
    fun parsePayload(request: HttpServletRequest): GitPayload?
}