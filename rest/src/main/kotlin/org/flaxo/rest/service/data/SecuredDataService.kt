package org.flaxo.rest.service.data

import org.flaxo.model.DataService
import org.flaxo.model.data.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

open class SecuredDataService(private val dataService: DataService,
                              private val passwordEncoder: PasswordEncoder
) : DataService by dataService {

    @Transactional
    override fun addUser(nickname: String,
                         password: String
    ): User = dataService.addUser(
            nickname,
            passwordEncoder.encode(password)
    )
}