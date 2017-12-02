package com.tcibinan.flaxo.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
        scanBasePackages = arrayOf("com.tcibinan.flaxo")
)
@EnableJpaRepositories("com.tcibinan.flaxo.core.dao")
@EntityScan("com.tcibinan.flaxo.core.model")
open class JpaTestApplication {
    @Bean open fun dataService(): DataService = DataServiceImpl()
}