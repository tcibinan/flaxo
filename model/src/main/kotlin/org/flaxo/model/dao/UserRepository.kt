package org.flaxo.model.dao

import org.flaxo.model.data.User
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for user entity.
 */
interface UserRepository : CrudRepository<User, Long> {

    /**
     * Finds a user by its [nickname].
     */
    fun findByNickname(nickname: String): User?

    /**
     * Finds a user by its [githubId].
     */
    fun findByGithubId(githubId: String): User?
}
