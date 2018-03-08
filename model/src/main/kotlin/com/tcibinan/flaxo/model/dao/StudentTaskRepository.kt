package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.StudentTaskEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student task entity.
 */
interface StudentTaskRepository : CrudRepository<StudentTaskEntity, Long>