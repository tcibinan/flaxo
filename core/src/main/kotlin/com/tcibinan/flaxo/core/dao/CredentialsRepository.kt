package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.CredentialsEntity
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<CredentialsEntity, Long> {
}