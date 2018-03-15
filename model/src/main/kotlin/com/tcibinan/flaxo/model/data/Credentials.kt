package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.CredentialsEntity

/**
 * Credentials data object.
 */
data class Credentials(private val entity: CredentialsEntity)
    : DataObject<CredentialsEntity> {

    val id: Long
            by lazy { entity.credentialsId ?: missing("id") }
    val password: String
            by lazy { entity.password ?: missing("password") }
    val githubToken: String?
            by lazy { entity.githubToken }
    val travisToken: String?
            by lazy { entity.travisToken }
    val codacyToken: String?
            by lazy { entity.codacyToken }

    override fun toEntity() = entity

}