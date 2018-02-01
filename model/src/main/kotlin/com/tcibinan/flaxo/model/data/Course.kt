package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.entity.CourseEntity

data class Course(val courseId: Long,
                  val name: String,
                  val language: String,
                  val testLanguage: String,
                  val testingFramework: String,
                  val status: String,
                  val user: User,
                  val students: Set<Student>,
                  val tasks: Set<Task>
) : DataObject<CourseEntity> {
    override fun toEntity() =
            CourseEntity(
                    courseId,
                    name,
                    language,
                    testLanguage,
                    testingFramework,
                    status,
                    user.toEntity(),
                    students.toEntities(),
                    tasks.toEntities()
            )
}