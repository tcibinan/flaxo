package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.data.Solution
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student task entity.
 */
interface SolutionRepository : CrudRepository<Solution, Long>