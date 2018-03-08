package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.StudentEntity
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student entity.
 */
interface StudentRepository : CrudRepository<StudentEntity, Long> {
    fun findByCourse(course: CourseEntity): Set<StudentEntity>
}