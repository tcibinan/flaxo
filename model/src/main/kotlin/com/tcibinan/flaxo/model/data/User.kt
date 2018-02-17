package com.tcibinan.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.UserEntity

@JsonIgnoreProperties("credentials")
data class User(private val entity: UserEntity) : DataObject<UserEntity> {
    val id: Long by lazy { entity.user_id ?: throw EntityFieldIsAbsent("user", "id") }
    val githubId: String? by lazy { entity.github_id }
    val nickname: String by lazy { entity.nickname ?: throw EntityFieldIsAbsent("user", "nickname") }
    val credentials: Credentials by lazy { Credentials(entity.credentials ?: throw EntityFieldIsAbsent("user", "credentials")) }

    override fun toEntity() = entity

    fun with(id: Long? = null,
             githubId: String? = null,
             nickname: String? = null,
             credentials: Credentials? = null
    ) = UserEntity()
            .apply {
                this.user_id = id ?: entity.user_id
                this.github_id = githubId ?: entity.github_id
                this.nickname = nickname ?: entity.nickname
                this.credentials = credentials?.toEntity() ?: entity.credentials
            }
            .toDto()
}