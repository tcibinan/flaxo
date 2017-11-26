package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.rest.model.Calculator
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.tcibinan.flaxo.rest")
class Application {
    @Bean fun calculator() = Calculator()
}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}