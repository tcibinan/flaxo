package org.flaxo.rest.manager.data

import org.flaxo.model.data.User
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

/**
 * Security user details.
 */
class UserDetailsImpl(val user: User) : UserDetails {

    override fun getAuthorities() = AuthorityUtils.createAuthorityList("USER").orEmpty()

    override fun getUsername() = user.name

    override fun getPassword() = user.credentials.password

    override fun isAccountNonLocked() = true

    override fun isAccountNonExpired() = true

    override fun isCredentialsNonExpired() = true

    override fun isEnabled() = true
}