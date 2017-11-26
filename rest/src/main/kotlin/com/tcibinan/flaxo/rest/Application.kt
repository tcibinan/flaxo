package com.tcibinan.flaxo.rest

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan("com.tcibinan.flaxo.rest")
class Application

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}