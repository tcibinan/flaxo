package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.Task
import org.springframework.data.repository.CrudRepository

interface TaskRepository : CrudRepository<Task, Long> {
}