package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.CascadeType
import javax.persistence.JoinColumn
import javax.persistence.Table

@Entity(name = "user")
@Table(name = "user")
class UserEntity: ConvertibleEntity<User> {
    @Id @GeneratedValue
    var user_id: Long? = null
    var nickname: String? = null
    @OneToOne(cascade = arrayOf(CascadeType.ALL)) @JoinColumn(name = "credentials_id")
    var credentials: CredentialsEntity? = null

    constructor(nickname: String, credentials: CredentialsEntity) {
        this.nickname = nickname
        this.credentials = credentials
    }

    constructor(user_id: Long, nickname: String, credentials: CredentialsEntity) {
        this.user_id = user_id
        this.nickname = nickname
        this.credentials = credentials
    }

    override fun toDto() = User(user_id!!, nickname!!, credentials!!.toDto())
}

data class User(
        val userId: Long,
        val nickname: String,
        val credentials: Credentials
) : DataObject<UserEntity> {
    override fun toEntity() = UserEntity(userId, nickname, credentials.toEntity())
}