package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.entity.StudentTaskEntity
import org.springframework.data.repository.CrudRepository

interface StudentTaskRepository : CrudRepository<StudentTaskEntity, Long> {
}