package org.flaxo.rest

import org.flaxo.rest.manager.message.MessageManager
import org.flaxo.rest.manager.message.SimpleMessageManager
import org.flaxo.rest.manager.response.ResponseManager
import org.flaxo.rest.manager.response.SimpleResponseManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.PropertySource
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

/**
 * Flaxo spring boot application.
 */
@SpringBootApplication(scanBasePackages = ["org.flaxo"])
@EnableJpaRepositories("org.flaxo.model.dao")
@EntityScan("org.flaxo.model.data")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:secured.properties", ignoreResourceNotFound = true)
class Application {

    @Bean
    fun messageService(messageSource: MessageSource): MessageManager = SimpleMessageManager(messageSource)

    @Bean
    fun responseService(): ResponseManager = SimpleResponseManager()

}