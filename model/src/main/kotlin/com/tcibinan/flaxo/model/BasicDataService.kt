package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.dao.CourseRepository
import com.tcibinan.flaxo.model.dao.CredentialsRepository
import com.tcibinan.flaxo.model.dao.StudentRepository
import com.tcibinan.flaxo.model.dao.StudentTaskRepository
import com.tcibinan.flaxo.model.dao.TaskRepository
import com.tcibinan.flaxo.model.dao.UserRepository
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User
import com.tcibinan.flaxo.model.entity.CourseEntity
import com.tcibinan.flaxo.model.entity.CredentialsEntity
import com.tcibinan.flaxo.model.entity.StudentEntity
import com.tcibinan.flaxo.model.entity.StudentTaskEntity
import com.tcibinan.flaxo.model.entity.TaskEntity
import com.tcibinan.flaxo.model.entity.UserEntity
import com.tcibinan.flaxo.model.entity.toDtos

class BasicDataService(private val userRepository: UserRepository,
                       private val credentialsRepository: CredentialsRepository,
                       private val courseRepository: CourseRepository,
                       private val taskRepository: TaskRepository,
                       private val studentRepository: StudentRepository,
                       private val studentTaskRepository: StudentTaskRepository
) : DataService {

    override fun addUser(nickname: String,
                         password: String
    ): User {
        if (userRepository.findByNickname(nickname) != null) {
            throw EntityAlreadyExistsException("User with '${nickname}' nickname already exists")
        }
        return userRepository
                .save(UserEntity().apply {
                    this.nickname = nickname
                    this.credentials = CredentialsEntity().apply { this.password = password }
                })
                .toDto()
    }

    override fun getUser(nickname: String): User? =
            userRepository.findByNickname(nickname)?.toDto()

    override fun getUserByGithubId(githubId: String): User? =
            userRepository.findByGithubId(githubId)?.toDto()

    override fun createCourse(courseName: String,
                              language: String,
                              testingLanguage: String,
                              testingFramework: String,
                              tasksPrefix: String,
                              numberOfTasks: Int,
                              owner: User
    ): Course {
        if (getCourse(courseName, owner) != null) {
            throw EntityAlreadyExistsException("${courseName} already exists for ${owner}")
        }
        val courseEntity = courseRepository
                .save(CourseEntity().apply {
                    this.name = courseName
                    this.language = language
                    this.testingLanguage = testingLanguage
                    this.testingFramework = testingFramework
                    this.status = CourseStatus.INIT
                    this.user = owner.toEntity()
                })
        for (i in 1..numberOfTasks) {
            taskRepository
                    .save(TaskEntity().apply {
                        this.taskName = "$tasksPrefix$i"
                        this.course = courseEntity
                    })
        }
        return getCourse(courseName, owner) ?: throw Exception("Could not create the course")
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
                studentRepository
                        .save(StudentEntity().apply {
                            this.nickname = nickname
                            this.course = course.toEntity()
                        })
                        .toDto()

        val tasks = taskRepository.findAllByCourse(course.toEntity())

        tasks.forEach {
            studentTaskRepository.save(StudentTaskEntity().apply {
                this.task = it
                this.student = student.toEntity()
            })
        }

        return studentRepository
                .findOne(student.id)
                ?.toDto()
                ?: throw Exception("Could not create the student $nickname for course ${course.user.nickname}/${course.name}.")
    }

    override fun getStudents(course: Course): Set<Student> =
            studentRepository.findByCourse(course.toEntity()).toDtos()

    override fun getTasks(course: Course): Set<Task> =
            taskRepository.findAllByCourse(course.toEntity()).toDtos()

    override fun addToken(userNickname: String,
                          service: IntegratedService,
                          accessToken: String
    ): User {
        getUser(userNickname)
                ?.credentials
                ?.toEntity()
                ?.withServiceToken(service, accessToken)
                ?.apply { credentialsRepository.save(this) }
                ?: throw Exception("Could not find user with $userNickname nickname.")

        return getUser(userNickname)
                ?: throw Exception("Could not find user with $userNickname nickname.")
    }

    override fun addGithubId(userNickname: String, githubId: String): User {
        val user = getUser(userNickname)
                ?: throw Exception("Could not find user with $userNickname nickname.")

        return userRepository.save(user.with(githubId = githubId).toEntity()).toDto()
    }

    override fun updateStudentTask(updatedStudentTask: StudentTask): StudentTask =
            studentTaskRepository.save(updatedStudentTask.toEntity()).toDto()

    override fun updateTask(updatedTask: Task): Task =
            taskRepository.save(updatedTask.toEntity()).toDto()
}

private fun CredentialsEntity.withServiceToken(service: IntegratedService,
                                               accessToken: String
): CredentialsEntity {
    when (service) {
        IntegratedService.GITHUB -> this.githubToken = accessToken
        IntegratedService.TRAVIS -> this.travisToken = accessToken
    }
    return this
}