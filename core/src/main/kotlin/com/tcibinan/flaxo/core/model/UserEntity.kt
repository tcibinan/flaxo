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
class UserEntity {
    @Id @GeneratedValue
    var user_id: Long? = null
    var nickname: String? = null
    @OneToOne(cascade = arrayOf(CascadeType.ALL)) @JoinColumn(name = "credentials_id")
    var credentials: CredentialsEntity? = null
}