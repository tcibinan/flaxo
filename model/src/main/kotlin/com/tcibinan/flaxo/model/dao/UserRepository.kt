package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByNickname(nickname: String): UserEntity?
    fun findByGithubId(githubId: String): UserEntity?
}