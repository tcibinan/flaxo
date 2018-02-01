package com.tcibinan.flaxo.rest.security

import com.tcibinan.flaxo.model.data.User
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(val user: User) : UserDetails {
    override fun getAuthorities() = AuthorityUtils.createAuthorityList("USER")
    override fun getUsername() = user.nickname
    override fun getPassword() = user.credentials.password
    override fun isAccountNonLocked() = true
    override fun isAccountNonExpired() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}