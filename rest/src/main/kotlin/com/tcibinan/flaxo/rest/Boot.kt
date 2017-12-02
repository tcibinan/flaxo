package com.tcibinan.flaxo.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
        scanBasePackages = arrayOf("com.tcibinan.flaxo")
)
@EnableJpaRepositories("com.tcibinan.flaxo.core.dao")
@EntityScan("com.tcibinan.flaxo.core.model")
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}