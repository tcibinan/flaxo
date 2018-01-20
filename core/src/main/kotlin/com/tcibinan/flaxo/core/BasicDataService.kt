package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.dao.*
import com.tcibinan.flaxo.core.model.*

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