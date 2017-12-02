package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.dao.CourseRepository
import com.tcibinan.flaxo.core.dao.StudentRepository
import com.tcibinan.flaxo.core.dao.TaskRepository
import com.tcibinan.flaxo.core.dao.UserRepository
import com.tcibinan.flaxo.core.model.Course
import com.tcibinan.flaxo.core.model.CourseEntity
import com.tcibinan.flaxo.core.model.CredentialsEntity
import com.tcibinan.flaxo.core.model.Student
import com.tcibinan.flaxo.core.model.StudentEntity
import com.tcibinan.flaxo.core.model.TaskEntity
import com.tcibinan.flaxo.core.model.User
import com.tcibinan.flaxo.core.model.UserEntity
import org.springframework.beans.factory.annotation.Autowired

internal class DataServiceImpl : DataService {
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var courseRepository: CourseRepository
    @Autowired lateinit var taskRepository: TaskRepository
    @Autowired lateinit var studentRepository: StudentRepository

    override fun addUser(nickname: String,
                         password: String): User =
            userRepository
                    .save(UserEntity(nickname = nickname, credentials = CredentialsEntity(password = password)))
                    .toDto()

    override fun getUser(nickname: String): User? =
            userRepository.findByNickname(nickname)?.toDto()

    override fun createCourse(
            name: String,
            language: String,
            testLanguage: String,
            testingFramework: String,
            numberOfTasks: Int,
            owner: User): Course {
        // TODO: rewrite using one to many approach. Now it might fall due to cascade type
        val courseEntity = courseRepository
                .save(CourseEntity(
                        name = name,
                        language = language,
                        test_language = testLanguage,
                        testing_framework = testingFramework,
                        user = owner.toEntity())
                )
        for (i in 1..numberOfTasks) {
            taskRepository.save(TaskEntity(task_name = "${name}-i", course = courseEntity))
        }
        return getCourse(name, owner) ?: throw RuntimeException("Could not create the course. Check cascade types")
    }

    override fun getCourse(name: String,
                           owner: User): Course? =
            courseRepository.findByNameAndUser(name, owner.toEntity())?.toDto()


    override fun addStudent(nickname: String,
                            course: Course): Student =
            studentRepository.save(StudentEntity(nickname = nickname, course = course.toEntity())).toDto()

    override fun getStudent(nickname: String): Student? =
            studentRepository.findByNickname(nickname)?.toDto()
}