package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CredentialsEntity
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<CredentialsEntity, Long> {
}