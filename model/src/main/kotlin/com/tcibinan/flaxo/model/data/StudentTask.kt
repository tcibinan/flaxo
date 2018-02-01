package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.StudentTaskEntity

data class StudentTask(val studentTaskId: Long,
                       val points: Int
) : DataObject<StudentTaskEntity> {
    override fun toEntity() = StudentTaskEntity(studentTaskId, points = points)
}