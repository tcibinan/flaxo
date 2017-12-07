package com.tcibinan.flaxo.rest.api

import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

data class ResponseWithMessageAndPayload(val code: Int, val message: String, val payload: Any) : Response
data class ResponseWithPayload(val code: Int, val payload: Any) : Response
data class ResponseWithMessage(val code: Int, val message: String) : Response
data class ResponseWithCode(val code: Int) : Response
interface Response

fun response(answer: ServerAnswer,
             message: String? = null,
             payload: Any? = null
): Response {
    if (payload == null) {
        if (message == null) {
            return ResponseWithCode(answer.code)
        } else {
            return ResponseWithMessage(answer.code, message)
        }
    } else {
        if (message == null) {
            return ResponseWithPayload(answer.code, payload)
        }
        return ResponseWithMessageAndPayload(answer.code, message, payload)
    }
}

fun redirect(path: String, model: ModelMap) =
        ModelAndView("redirect:/${path}", model)