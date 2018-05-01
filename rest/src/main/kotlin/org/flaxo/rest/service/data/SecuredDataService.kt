package org.flaxo.rest.service.data

import org.flaxo.model.DataService
import org.flaxo.model.data.User
import org.springframework.security.crypto.password.PasswordEncoder

class SecuredDataService(private val dataService: DataService,
                         private val passwordEncoder: PasswordEncoder
) : DataService by dataService {

    override fun addUser(nickname: String,
                         password: String
    ): User = dataService.addUser(
            nickname,
            passwordEncoder.encode(password)
    )
}