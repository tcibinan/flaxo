package com.tcibinan.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.tcibinan.flaxo.model.entity.UserEntity

@JsonIgnoreProperties("credentials")
data class User(val userId: Long,
                val nickname: String,
                val credentials: Credentials
) : DataObject<UserEntity> {
    override fun toEntity() = UserEntity(userId, nickname, credentials.toEntity())
}