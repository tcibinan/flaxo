package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.data.Credentials
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for credentials entity.
 */
interface CredentialsRepository : CrudRepository<Credentials, Long>