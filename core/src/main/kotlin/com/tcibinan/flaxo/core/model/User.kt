package com.tcibinan.flaxo.core.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.JoinColumn
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity(name = "user")
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["nickname"])])
class UserEntity(): ConvertibleEntity<User> {
    @Id @GeneratedValue
    var user_id: Long? = null
    var nickname: String? = null
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "credentials_id")
    var credentials: CredentialsEntity? = null

    constructor(user_id: Long? = null, nickname: String, credentials: CredentialsEntity) : this() {
        this.user_id = user_id
        this.nickname = nickname
        this.credentials = credentials
    }

    override fun toDto() = User(user_id!!, nickname!!, credentials!!.toDto())
}

@JsonIgnoreProperties("credentials")
data class User(
        val userId: Long,
        val nickname: String,
        val credentials: Credentials
) : DataObject<UserEntity> {
    override fun toEntity() = UserEntity(userId, nickname, credentials.toEntity())
}