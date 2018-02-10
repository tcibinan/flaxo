package com.tcibinan.flaxo.rest.service.response

import com.tcibinan.flaxo.rest.api.ServerAnswer

interface ResponseService {

    fun response(answer: ServerAnswer,
                 vararg args: String?,
                 payload: Any? = null
    ): Response

}

