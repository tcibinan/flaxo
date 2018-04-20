package org.flaxo.model

import org.flaxo.model.dao.CourseRepository
import org.flaxo.model.dao.CredentialsRepository
import org.flaxo.model.dao.StudentRepository
import org.flaxo.model.dao.SolutionRepository
import org.flaxo.model.dao.TaskRepository
import org.flaxo.model.dao.UserRepository
import org.flaxo.model.data.Course
import org.flaxo.model.data.CourseState
import org.flaxo.model.data.Credentials
import org.flaxo.model.data.Student
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Task
import org.flaxo.model.data.User

/**
 * Data service implementation based on jpa repositories.
 */
class BasicDataService(private val userRepository: UserRepository,
                       private val credentialsRepository: CredentialsRepository,
                       private val courseRepository: CourseRepository,
                       private val taskRepository: TaskRepository,
                       private val studentRepository: StudentRepository,
                       private val solutionRepository: SolutionRepository
) : DataService {

    override fun addUser(nickname: String,
                         password: String
    ): User {
        if (userRepository.findByNickname(nickname) != null)
            throw EntityAlreadyExistsException("User $nickname")

        return userRepository
                .save(User(nickname = nickname, credentials = Credentials(password = password)))
    }

    override fun getUser(nickname: String): User? =
            userRepository.findByNickname(nickname)

    override fun getUserByGithubId(githubId: String): User? =
            userRepository.findByGithubId(githubId)

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
                .save(Course(
                        name = courseName,
                        language = language,
                        testingLanguage = testingLanguage,
                        testingFramework = testingFramework,
                        state = CourseState(),
                        user = owner
                ))

        for (i in 1..numberOfTasks) {
            taskRepository
                    .save(Task(name = "$tasksPrefix$i", course = courseEntity))
        }

        return getCourse(courseName, owner)
                ?: throw ModelException("Could not create the course")
    }

    override fun deleteCourse(courseName: String,
                              owner: User
    ) {
        getCourse(courseName, owner)
                ?.also { courseRepository.delete(it) }
                ?: throw EntityNotFound("Repository $courseName")
    }

    override fun updateCourse(updatedCourse: Course): Course =
            courseRepository.save(updatedCourse)

    override fun getCourse(name: String,
                           owner: User
    ): Course? =
            courseRepository.findByNameAndUser(name, owner)

    override fun getCourses(userNickname: String): Set<Course> {
        val user = getUser(userNickname)
                ?: userNotFound(userNickname)

        return courseRepository.findByUser(user)
    }

    override fun addStudent(nickname: String,
                            course: Course): Student {
        val student =
                studentRepository.save(Student(nickname = nickname, course = course))

        taskRepository
                .findAllByCourse(course)
                .forEach { task -> solutionRepository.save(Solution(task = task, student = student)) }

        return studentRepository
                .findById(student.id)
                .orElseThrow {
                    ModelException("Could not create the student $nickname " +
                            "for course ${course.user.nickname}/${course.name}")
                }
    }

    override fun getStudents(course: Course): Set<Student> =
            studentRepository.findByCourse(course)

    override fun getSolutions(student: Student): Set<Solution> =
            solutionRepository.findByStudent(student)

    override fun getTasks(course: Course): Set<Task> =
            taskRepository.findAllByCourse(course)

    override fun addToken(userNickname: String,
                          service: IntegratedService,
                          accessToken: String
    ): User =
            getUser(userNickname)
                    ?.apply {
                        credentials
                                .withServiceToken(service, accessToken)
                                .also { credentialsRepository.save(it) }
                    }
                    ?: userNotFound(userNickname)

    override fun addGithubId(userNickname: String, githubId: String): User {
        val user: User = getUser(userNickname)
                ?: userNotFound(userNickname)

        return userRepository.save(user.copy(githubId = githubId))
    }

    private fun userNotFound(userNickname: String): Nothing {
        throw ModelException("Could not find user with $userNickname nickname")
    }

    override fun updateSolution(updatedSolution: Solution): Solution =
            solutionRepository.save(updatedSolution)

    override fun updateTask(updatedTask: Task): Task =
            taskRepository.save(updatedTask)
}

private fun Credentials.withServiceToken(service: IntegratedService,
                                         accessToken: String
): Credentials = run {
    when (service) {
        IntegratedService.GITHUB -> copy(githubToken = accessToken)
        IntegratedService.TRAVIS -> copy(travisToken = accessToken)
        IntegratedService.CODACY -> copy(codacyToken = accessToken)
    }
}