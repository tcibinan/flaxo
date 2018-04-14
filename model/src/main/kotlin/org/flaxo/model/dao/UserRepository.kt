package org.flaxo.model.dao

import org.flaxo.model.data.User
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for user entity.
 */
interface UserRepository : CrudRepository<User, Long> {
    fun findByNickname(nickname: String): User?
    fun findByGithubId(githubId: String): User?
}