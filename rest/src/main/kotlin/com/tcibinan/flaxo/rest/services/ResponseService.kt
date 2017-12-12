package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.rest.api.ServerAnswer
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

interface ResponseService {

    fun response(answer: ServerAnswer,
                 vararg args: String?,
                 payload: Any? = null
    ): Response

    fun redirect(path: String, model: ModelMap): ModelAndView
}

interface Response