package org.flaxo.model.dao

import org.flaxo.model.data.Solution
import org.flaxo.model.data.Student
import org.flaxo.model.data.Task
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for student task entity.
 */
interface SolutionRepository : CrudRepository<Solution, Long> {

    /**
     * Finds all [student] solutions.
     */
    fun findByStudent(student: Student): Set<Solution>

    /**
     * Finds all [task] solutions.
     */
    fun findByTask(task: Task): Set<Solution>
}
