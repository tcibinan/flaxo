package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Credentials(
        @Id @GeneratedValue
        val credentials_id: Long,
        val password: String,
        val github_token: String?,
        val travis_token: String?,
        val codacy_token: String?
)