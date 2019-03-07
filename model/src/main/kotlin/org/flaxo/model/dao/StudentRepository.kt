package org.flaxo.model.dao

import org.flaxo.model.data.Course
import org.flaxo.model.data.Student
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student entity.
 */
interface StudentRepository : CrudRepository<Student, Long> {

    /**
     * Finds all [course] students.
     */
    fun findByCourse(course: Course): Set<Student>
}