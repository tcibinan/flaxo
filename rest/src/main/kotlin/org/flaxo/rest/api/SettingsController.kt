package org.flaxo.rest.api

import org.flaxo.core.language.Language
import org.flaxo.rest.service.response.ResponseService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/settings")
class SettingsController(private val responseService: ResponseService,
                         private val supportedLanguages: Map<String, Language>
) {

    /**
     * Returns a list of supported languages by flaxo.
     */
    @GetMapping("/languages")
    fun supportedLanguages(): ResponseEntity<Any> =
            supportedLanguages
                    .map { (name, language) ->
                        object {
                            val name = name
                            val compatibleTestingLanguages =
                                    language.compatibleTestingLanguages.map { it.name }
                            val compatibleTestingFrameworks =
                                    language.compatibleTestingFrameworks.map { it.name }
                        }
                    }
                    .let { responseService.ok(it) }
}