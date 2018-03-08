package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CredentialsEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for credentials entity.
 */
interface CredentialsRepository : CrudRepository<CredentialsEntity, Long>