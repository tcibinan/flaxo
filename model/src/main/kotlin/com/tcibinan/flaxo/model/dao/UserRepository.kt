package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.UserEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for user entity.
 */
interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByNickname(nickname: String): UserEntity?
    fun findByGithubId(githubId: String): UserEntity?
}