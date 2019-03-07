package org.flaxo.model.dao

import org.flaxo.model.data.Credentials
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for credentials entity.
 */
interface CredentialsRepository : CrudRepository<Credentials, Long>
