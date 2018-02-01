package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.StudentTaskEntity
import org.springframework.data.repository.CrudRepository

interface StudentTaskRepository : CrudRepository<StudentTaskEntity, Long> {
}