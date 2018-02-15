package com.tcibinan.flaxo.rest

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:secured.properties", ignoreResourceNotFound = true)
@Configuration
class TestApplication