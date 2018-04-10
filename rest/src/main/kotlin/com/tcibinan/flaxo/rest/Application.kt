package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.rest.service.message.MessageService
import com.tcibinan.flaxo.rest.service.message.NaiveMessageService
import com.tcibinan.flaxo.rest.service.response.ResponseService
import com.tcibinan.flaxo.rest.service.response.SimpleResponseService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@SpringBootApplication(scanBasePackages = ["com.tcibinan.flaxo"])
@EnableJpaRepositories("com.tcibinan.flaxo.model.dao")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan("com.tcibinan.flaxo.model.data")
@PropertySource("classpath:secured.properties", ignoreResourceNotFound = true)
class Application {

    @Bean
    fun messageService(messageSource: MessageSource): MessageService = NaiveMessageService(messageSource)

    @Bean
    fun responseService(messageService: MessageService): ResponseService = SimpleResponseService(messageService)

}