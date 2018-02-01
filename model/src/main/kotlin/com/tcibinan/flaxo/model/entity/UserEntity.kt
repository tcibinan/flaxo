package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.User
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity(name = "user")
@Table(name = "user", uniqueConstraints = [(UniqueConstraint(columnNames = ["nickname"]))])
class UserEntity() : ConvertibleEntity<User> {
    @Id
    @GeneratedValue
    var user_id: Long? = null
    var nickname: String? = null
    @OneToOne(cascade = [(CascadeType.ALL)])
    var credentials: CredentialsEntity? = null

    constructor(user_id: Long? = null,
                nickname: String,
                credentials: CredentialsEntity
    ) : this() {
        this.user_id = user_id
        this.nickname = nickname
        this.credentials = credentials
    }

    override fun toDto() = User(user_id!!, nickname!!, credentials!!.toDto())
}