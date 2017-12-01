package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.StudentTask
import org.springframework.data.repository.CrudRepository

interface StudentTaskRepository : CrudRepository<StudentTask, Long> {
}