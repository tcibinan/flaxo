package org.flaxo.rest

import org.flaxo.rest.service.message.MessageService
import org.flaxo.rest.service.message.NaiveMessageService
import org.flaxo.rest.service.response.ResponseService
import org.flaxo.rest.service.response.SimpleResponseService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@SpringBootApplication(scanBasePackages = ["org.flaxo"])
@EnableJpaRepositories("org.flaxo.model.dao")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan("org.flaxo.model.data")
@PropertySource("classpath:secured.properties", ignoreResourceNotFound = true)
class Application {

    @Bean
    fun messageService(messageSource: MessageSource): MessageService = NaiveMessageService(messageSource)

    @Bean
    fun responseService(): ResponseService = SimpleResponseService()

}