package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
}