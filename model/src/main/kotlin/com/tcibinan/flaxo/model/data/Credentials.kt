package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent
import com.tcibinan.flaxo.model.entity.CredentialsEntity

data class Credentials(private val entity: CredentialsEntity) : DataObject<CredentialsEntity> {
    val id: Long by lazy { entity.credentialsId ?: throw EntityFieldIsAbsent("credentials", "id") }
    val password: String by lazy { entity.password ?: throw EntityFieldIsAbsent("credentials", "password") }
    val githubToken: String? by lazy { entity.githubToken }
    val travisToken: String? by lazy { entity.travisToken }
    val codacyToken: String? by lazy { entity.codacyToken }

    override fun toEntity() = entity

}