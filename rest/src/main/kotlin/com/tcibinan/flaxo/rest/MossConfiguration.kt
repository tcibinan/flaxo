package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.rest.service.git.GitService
import com.tcibinan.flaxo.rest.service.moss.MossService
import com.tcibinan.flaxo.rest.service.moss.SimpleMossService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MossConfiguration {

    @Bean
    fun mossService(@Value("\${MOSS_USER_ID}") userId: String,
                    gitService: GitService,
                    supportedLanguages: Map<String, Language>
    ): MossService =
            SimpleMossService(userId, gitService, supportedLanguages)
}