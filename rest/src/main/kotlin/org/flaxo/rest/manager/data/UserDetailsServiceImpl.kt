package org.flaxo.rest.manager.data

import org.flaxo.model.DataManager
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

/**
 * Security user details service.
 */
class UserDetailsServiceImpl(private val dataManager: DataManager) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails =
            dataManager.getUser(username)
                    ?.let { UserDetailsImpl(it) }
                    ?: throw UsernameNotFoundException(username)

}