package org.flaxo.model.dao

import org.flaxo.model.data.Commit
import org.springframework.data.repository.CrudRepository

/**
 * Commit entity crud repository.
 */
interface CommitRepository: CrudRepository<Commit, Long>