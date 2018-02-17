package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.CredentialsEntity

data class Credentials(private val entity: CredentialsEntity) : DataObject<CredentialsEntity> {
    val id: Long by lazy { entity.credentials_id ?: throw EntityFieldIsAbsent("credentials", "id") }
    val password: String by lazy { entity.password ?: throw EntityFieldIsAbsent("credentials", "password") }
    val githubToken: String? by lazy { entity.github_token }
    val travisToken: String? by lazy { entity.travis_token }
    val codacyToken: String? by lazy { entity.codacy_token }

    override fun toEntity() = entity

}