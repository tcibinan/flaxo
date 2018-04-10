package com.tcibinan.flaxo.model.dao

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Student
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student entity.
 */
interface StudentRepository : CrudRepository<Student, Long> {
    fun findByCourse(course: Course): Set<Student>
}