package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByNickname(nickname: String): UserEntity?
}