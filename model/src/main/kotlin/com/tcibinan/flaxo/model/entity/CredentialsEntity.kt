package com.tcibinan.flaxo.model.entity

import com.tcibinan.flaxo.model.data.Credentials
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "credentials")
@Table(name = "credentials")
class CredentialsEntity() : ConvertibleEntity<Credentials> {

    @Id
    @GeneratedValue
    var credentialsId: Long? = null
    var password: String? = null
    var githubToken: String? = null
    var travisToken: String? = null
    var codacyToken: String? = null

    override fun toDto() = Credentials(this)
}