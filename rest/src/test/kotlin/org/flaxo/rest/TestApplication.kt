package org.flaxo.rest

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:flaxo.properties", ignoreResourceNotFound = true)
@Configuration
class TestApplication