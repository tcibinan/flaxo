package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
}