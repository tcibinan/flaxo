package org.flaxo.rest.api

import org.flaxo.common.lang.Language
import org.flaxo.model.LanguageView
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Flaxo settings controller.
 */
@RestController
@RequestMapping("/rest/settings")
class SettingsController(private val responseManager: ResponseManager,
                         private val languages: List<Language>
) {

    /**
     * Returns a list of supported languages by flaxo.
     */
    @GetMapping("/languages")
    fun languages(): Response<List<LanguageView>> =
            languages
                    .map { language ->
                        LanguageView(
                                name = language.name,
                                compatibleTestingLanguages = language.compatibleTestingLanguages.map { it.name },
                                compatibleTestingFrameworks = language.compatibleTestingFrameworks.map { it.name }
                        )
                    }
                    .let { responseManager.ok(it) }
}
