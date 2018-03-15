package com.tcibinan.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tcibinan.flaxo.model.entity.UserEntity

/**
 * User data object.
 */
@JsonIgnoreProperties("credentials")
data class User(private val entity: UserEntity)
    : DataObject<UserEntity> {

    val id: Long
            by lazy { entity.userId ?: missing("id") }
    val githubId: String?
            by lazy { entity.githubId }
    val nickname: String
            by lazy { entity.nickname ?: missing("nickname") }
    val credentials: Credentials
            by lazy { Credentials(entity.credentials ?: missing("credentials")) }

    override fun toEntity() = entity

    override fun view(): Any = let { user ->
        object {
            val id = user.id
            val githubId = user.githubId
            val nickname = user.nickname
            val isGithubAuthorized = user.credentials.githubToken != null
            val isTravisAuthorized = user.credentials.travisToken != null
            val isCodacyAuthorized = user.credentials.codacyToken != null
        }
    }

    fun with(id: Long? = null,
             githubId: String? = null,
             nickname: String? = null,
             credentials: Credentials? = null
    ) = UserEntity()
            .apply {
                this.userId = id ?: entity.userId
                this.githubId = githubId ?: entity.githubId
                this.nickname = nickname ?: entity.nickname
                this.credentials = credentials?.toEntity() ?: entity.credentials
            }
            .toDto()
}