package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity(name = "credentials")
@Table(name = "credentials")
class CredentialsEntity {
    @Id @GeneratedValue
    var credentials_id: Long? = null
    var password: String? = null
    var github_token: String? = null
    var travis_token: String? = null
    var codacy_token: String? = null
}