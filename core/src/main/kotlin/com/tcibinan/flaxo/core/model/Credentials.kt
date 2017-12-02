package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "credentials")
@Table(name = "credentials")
class CredentialsEntity : ConvertibleEntity<Credentials> {
    @Id @GeneratedValue
    var credentials_id: Long? = null
    var password: String? = null
    var github_token: String? = null
    var travis_token: String? = null
    var codacy_token: String? = null

    constructor(credentials_id: Long? = null,
                password: String,
                github_token: String? = null,
                travis_token: String? = null,
                codacy_token: String? = null) {
        this.credentials_id = credentials_id
        this.password = password
        this.github_token = github_token
        this.travis_token = travis_token
        this.codacy_token = codacy_token
    }

    override fun toDto() = Credentials(credentials_id!!, password!!, github_token, travis_token, codacy_token)
}

data class Credentials (
        val credentialsId: Long,
        val password: String,
        val githubToken: String? = null,
        val travisToken: String? = null,
        val codacyToken: String? = null
) : DataObject<CredentialsEntity> {
    override fun toEntity() = CredentialsEntity(credentialsId, password, githubToken, travisToken, codacyToken)
}