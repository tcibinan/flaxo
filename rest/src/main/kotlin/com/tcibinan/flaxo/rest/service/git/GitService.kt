package com.tcibinan.flaxo.rest.service.git

import com.tcibinan.flaxo.git.GitInstance
import com.tcibinan.flaxo.git.GitPayload
import javax.servlet.http.HttpServletRequest

interface GitService {
    fun with(credentials: String): GitInstance
    fun parsePayload(request: HttpServletRequest): GitPayload?
}