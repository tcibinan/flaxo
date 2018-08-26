package org.flaxo.rest.manager.data

import org.flaxo.model.DataManager
import org.flaxo.model.data.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional

/**
 * Data service wrapper with passwords encoding.
 */
open class SecuredDataManager(private val dataManager: DataManager,
                              private val passwordEncoder: PasswordEncoder
) : DataManager by dataManager {

    @Transactional
    override fun addUser(nickname: String, password: String): User =
            dataManager.addUser(nickname, passwordEncoder.encode(password))
}