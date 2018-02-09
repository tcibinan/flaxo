package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.StudentTaskEntity
import com.tcibinan.flaxo.model.entity.UserEntity
import com.tcibinan.flaxo.model.dao.CourseRepository
import com.tcibinan.flaxo.model.dao.StudentRepository
import com.tcibinan.flaxo.model.dao.StudentTaskRepository
import com.tcibinan.flaxo.model.dao.TaskRepository
import com.tcibinan.flaxo.model.dao.UserRepository
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.CourseStatus
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User
import com.tcibinan.flaxo.model.entity.CredentialsEntity
import com.tcibinan.flaxo.model.entity.StudentEntity
import com.tcibinan.flaxo.model.entity.TaskEntity
import com.tcibinan.flaxo.model.entity.toDtos

class BasicDataService(val userRepository: UserRepository,
                       val courseRepository: CourseRepository,
                       val taskRepository: TaskRepository,
                       val studentRepository: StudentRepository,
                       val studentTaskRepository: StudentTaskRepository
) : DataService {

    override fun addUser(nickname: String,
                         password: String
    ): User {
        if (userRepository.findByNickname(nickname) != null) {
            throw EntityAlreadyExistsException("User with '${nickname}' nickname already exists")
        }
        return userRepository
                .save(UserEntity(nickname = nickname, credentials = CredentialsEntity(password = password)))
                .toDto()
    }

    override fun getUser(nickname: String): User? =
            userRepository.findByNickname(nickname)?.toDto()

    override fun createCourse(name: String,
                              language: String,
                              testLanguage: String,
                              testingFramework: String,
                              numberOfTasks: Int,
                              owner: User
    ): Course {
        if (getCourse(name, owner) != null) {
            throw EntityAlreadyExistsException("${name} already exists for ${owner}")
        }
        val courseEntity = courseRepository
                .save(
                        CourseEntity(
                                name = name,
                                language = language,
                                test_language = testLanguage,
                                testing_framework = testingFramework,
                                status = CourseStatus.INIT,
                                user = owner.toEntity()
                        )
                )
        for (i in 1..numberOfTasks) {
            taskRepository.save(TaskEntity(task_name = "${name}-$i", course = courseEntity))
        }
        return getCourse(name, owner) ?: throw Exception("Could not create the course")
    }

    override fun deleteCourse(courseName: String,
                              owner: User
    ) {
        val course = getCourse(courseName, owner)
                ?: throw EntityNotFound("Repository $courseName")

        courseRepository.delete(course.toEntity())
    }

    override fun updateCourse(updatedCourse: Course) {
        courseRepository.save(updatedCourse.toEntity())
    }

    override fun getCourse(name: String,
                           owner: User
    ): Course? =
            courseRepository.findByNameAndUser(name, owner.toEntity())?.toDto()

    override fun getCourses(userNickname: String): Set<Course> {
        val user = getUser(userNickname)
        user ?: throw Exception("Could not find user with $userNickname nickname")

        return courseRepository.findByUser(user.toEntity()).toDtos()
    }

    override fun addStudent(nickname: String,
                            course: Course): Student {
        val student =
                studentRepository.save(StudentEntity(nickname = nickname, course = course.toEntity())).toDto()

        val tasks = taskRepository.findAllByCourse(course.toEntity())

        tasks.forEach {
            studentTaskRepository.save(StudentTaskEntity(task = it, student = student.toEntity()))
        }

        return studentRepository
                .findById(student.studentId)
                .map { it.toDto() }
                .orElseThrow { Exception("Could not create the student") }
    }

    override fun getStudents(course: Course): Set<Student> =
            studentRepository.findByCourse(course.toEntity()).toDtos()

    override fun getTasks(course: Course): Set<Task> =
            taskRepository.findAllByCourse(course.toEntity()).toDtos()
}