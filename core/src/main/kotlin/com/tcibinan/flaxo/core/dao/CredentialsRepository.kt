package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.CredentialsEntity
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<CredentialsEntity, Long> {
}