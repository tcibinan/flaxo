package com.tcibinan.flaxo.rest.service.data

import com.tcibinan.flaxo.model.DataService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class UserDetailsServiceImpl(
        val dataService: DataService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = dataService.getUser(username)
        user ?: throw UsernameNotFoundException(username)

        return UserDetailsImpl(user)
    }

}