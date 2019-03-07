package org.flaxo.rest

import org.flaxo.model.DataManager
import org.flaxo.rest.filters.FlaxoCorsFilter
import org.flaxo.rest.manager.data.UserDetailsServiceImpl
import org.flaxo.rest.manager.data.WebSecurityConfigurerImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Security configuration.
 */
@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    fun userDetailsService(dataManager: DataManager): UserDetailsService = UserDetailsServiceImpl(dataManager)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun webSecurityConfigurer(
            userDetailsService: UserDetailsService,
            passwordEncoder: PasswordEncoder
    ): WebSecurityConfigurer<WebSecurity> =
            WebSecurityConfigurerImpl(userDetailsService, passwordEncoder)

    @Bean
    fun corsFilter() = FlaxoCorsFilter()

}

