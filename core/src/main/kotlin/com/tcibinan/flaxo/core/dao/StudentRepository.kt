package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.Student
import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, Long> {
}