package com.tcibinan.flaxo.core.model

import com.tcibinan.flaxo.core.entity.CredentialsEntity

data class Credentials(val credentialsId: Long,
                       val password: String,
                       val githubToken: String? = null,
                       val travisToken: String? = null,
                       val codacyToken: String? = null
) : DataObject<CredentialsEntity> {
    override fun toEntity() = CredentialsEntity(credentialsId, password, githubToken, travisToken, codacyToken)
}