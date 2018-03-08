package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.User
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

/**
 * User entity object.
 */
@Entity(name = "user")
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["nickname"])])
class UserEntity : EntityObject<User> {

    @Id
    @GeneratedValue
    var userId: Long? = null
    var nickname: String? = null
    var githubId: String? = null
    @OneToOne(cascade = [(CascadeType.ALL)])
    var credentials: CredentialsEntity? = null

    override fun toDto() = User(this)
}