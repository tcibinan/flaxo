package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.rest.security.UserDetailsServiceImpl
import com.tcibinan.flaxo.rest.security.WebSecurityConfigurerImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
class SecurityConfiguration {
    @Bean
    fun userDetailsService(dataService: DataService): UserDetailsService =
            UserDetailsServiceImpl(dataService)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun webSecurityConfigurer(
            userDetailsService: UserDetailsService,
            passwordEncoder: PasswordEncoder
    ): WebSecurityConfigurer<WebSecurity> =
            WebSecurityConfigurerImpl(userDetailsService, passwordEncoder)
}

