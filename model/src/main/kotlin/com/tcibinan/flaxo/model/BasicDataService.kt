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

/**
 * Data service implementation based on jpa repositories.
 */
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
        if (userRepository.findByNickname(nickname) != null)
            throw EntityAlreadyExistsException("User $nickname")

        return userRepository
                .save(UserEntity().also {
                    it.nickname = nickname
                    it.credentials = CredentialsEntity().also { it.password = password }
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
        if (getCourse(courseName, owner) != null)
            throw EntityAlreadyExistsException("Course $owner/$courseName")

        val courseEntity = courseRepository
                .save(CourseEntity().also {
                    it.name = courseName
                    it.language = language
                    it.testingLanguage = testingLanguage
                    it.testingFramework = testingFramework
                    it.status = CourseStatus.INIT
                    it.user = owner.toEntity()
                })

        for (i in 1..numberOfTasks) {
            taskRepository
                    .save(TaskEntity().also {
                        it.taskName = "$tasksPrefix$i"
                        it.course = courseEntity
                    })
        }

        return getCourse(courseName, owner)
                ?: throw ModelException("Could not create the course")
    }

    override fun deleteCourse(courseName: String,
                              owner: User
    ) {
        getCourse(courseName, owner)
                ?.toEntity()
                ?.also { courseRepository.delete(it) }
                ?: throw EntityNotFound("Repository $courseName")
    }

    override fun updateCourse(updatedCourse: Course): Course =
            courseRepository.save(updatedCourse.toEntity()).toDto()

    override fun getCourse(name: String,
                           owner: User
    ): Course? =
            courseRepository.findByNameAndUser(name, owner.toEntity())?.toDto()

    override fun getCourses(userNickname: String): Set<Course> {
        val user = getUser(userNickname)
                ?: userNotFound(userNickname)

        return courseRepository.findByUser(user.toEntity()).toDtos()
    }

    override fun addStudent(nickname: String,
                            course: Course): Student {
        val student =
                studentRepository
                        .save(StudentEntity().also {
                            it.nickname = nickname
                            it.course = course.toEntity()
                        })
                        .toDto()

        taskRepository
                .findAllByCourse(course.toEntity())
                .forEach { task ->
                    studentTaskRepository
                            .save(StudentTaskEntity().also {
                                it.task = task
                                it.student = student.toEntity()
                            })
                }

        return studentRepository
                .findById(student.id)
                .map { it.toDto() }
                .orElseThrow {
                    ModelException("Could not create the student $nickname " +
                            "for course ${course.user.nickname}/${course.name}")
                }
    }

    override fun getStudents(course: Course): Set<Student> =
            studentRepository.findByCourse(course.toEntity()).toDtos()

    override fun getTasks(course: Course): Set<Task> =
            taskRepository.findAllByCourse(course.toEntity()).toDtos()

    override fun addToken(userNickname: String,
                          service: IntegratedService,
                          accessToken: String
    ): User =
            getUser(userNickname)
                    ?.apply {
                        credentials.toEntity()
                                .withServiceToken(service, accessToken)
                                .also { credentialsRepository.save(it) }
                    }
                    ?: userNotFound(userNickname)

    override fun addGithubId(userNickname: String, githubId: String): User {
        val user: User = getUser(userNickname)
                ?: userNotFound(userNickname)

        return userRepository.save(user.with(githubId = githubId).toEntity()).toDto()
    }

    private fun userNotFound(userNickname: String): Nothing {
        throw ModelException("Could not find user with $userNickname nickname")
    }

    override fun updateStudentTask(updatedStudentTask: StudentTask): StudentTask =
            studentTaskRepository.save(updatedStudentTask.toEntity()).toDto()

    override fun updateTask(updatedTask: Task): Task =
            taskRepository.save(updatedTask.toEntity()).toDto()
}

private fun CredentialsEntity.withServiceToken(service: IntegratedService,
                                               accessToken: String
): CredentialsEntity = also {
    when (service) {
        IntegratedService.GITHUB -> it.githubToken = accessToken
        IntegratedService.TRAVIS -> it.travisToken = accessToken
    }
}