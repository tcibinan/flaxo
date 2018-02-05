package com.tcibinan.flaxo.rest.service.data

import com.tcibinan.flaxo.model.DataService
import org.springframework.security.crypto.password.PasswordEncoder

class SecuredDataService(
        val dataService: DataService,
        val passwordEncoder: PasswordEncoder
) : DataService by dataService {
    override fun addUser(nickname: String, password: String) =
            dataService.addUser(nickname, passwordEncoder.encode(password))
}