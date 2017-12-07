package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.rest.security.SecuredDataService
import com.tcibinan.flaxo.rest.services.MessageService
import com.tcibinan.flaxo.rest.services.NaiveMessageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.password.PasswordEncoder

@SpringBootApplication(
        scanBasePackages = arrayOf("com.tcibinan.flaxo")
)
@EnableJpaRepositories("com.tcibinan.flaxo.core.dao")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EntityScan("com.tcibinan.flaxo.core.model")
class Application {

    @Bean("nonSecuredDataService") fun dataService() = DataService.default()

    @Bean fun dataService(nonSecuredDataService: DataService, passwordEncoder: PasswordEncoder): DataService =
            SecuredDataService(nonSecuredDataService, passwordEncoder)

    @Bean fun messageService(): MessageService = NaiveMessageService()
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}