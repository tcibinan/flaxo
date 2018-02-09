package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.rest.service.data.UserDetailsServiceImpl
import com.tcibinan.flaxo.rest.service.data.WebSecurityConfigurerImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.WebSecurityConfigurer
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Value("\${HOME_PAGE}")
    lateinit var guiUrl: String

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

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf(guiUrl)
        configuration.allowedMethods = listOf("GET", "POST")
        configuration.allowCredentials = true;
        configuration.allowedHeaders = listOf("Authorization", "Cache-Control", "Content-Type");
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}

