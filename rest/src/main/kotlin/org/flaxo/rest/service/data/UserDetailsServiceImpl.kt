package org.flaxo.rest.service.data

import org.flaxo.model.DataService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Security user details service.
 */
class UserDetailsServiceImpl(
        private val dataService: DataService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
            dataService.getUser(username)
                    ?.let { UserDetailsImpl(it) }
                    ?: throw UsernameNotFoundException(username)

}