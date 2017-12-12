package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.rest.api.ServerAnswer
import org.springframework.ui.ModelMap
import org.springframework.web.servlet.ModelAndView

class SimpleResponseService(
        val messageService: MessageService
) : ResponseService {

    override fun response(
            answer: ServerAnswer,
            vararg args: String?,
            payload: Any?
    ): Response {
        return if (payload == null) {
            if (answer.defaultAnswer == null) ResponseWithCode(answer.code)
            else ResponseWithMessage(answer.code, messageService.get(answer.defaultAnswer, *args))
        } else {
            if (answer.defaultAnswer == null) ResponseWithPayload(answer.code, payload)
            else ResponseWithMessageAndPayload(answer.code, messageService.get(answer.defaultAnswer, *args), payload)
        }
    }

    override fun redirect(path: String, model: ModelMap) =
            ModelAndView("redirect:/${path}", model)
}

data class ResponseWithMessageAndPayload(val code: Int, val message: String, val payload: Any) : Response
data class ResponseWithPayload(val code: Int, val payload: Any) : Response
data class ResponseWithMessage(val code: Int, val message: String) : Response
data class ResponseWithCode(val code: Int) : Response