package com.tcibinan.flaxo.core.model

import com.tcibinan.flaxo.core.entity.StudentTaskEntity

data class StudentTask(val studentTaskId: Long,
                       val points: Int
) : DataObject<StudentTaskEntity> {
    override fun toEntity() = StudentTaskEntity(studentTaskId, points = points)
}