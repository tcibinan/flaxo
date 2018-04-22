package org.flaxo.model

import org.flaxo.model.dao.BuildReportRepository
import org.flaxo.model.dao.CodeStyleReportRepository
import org.flaxo.model.dao.CourseRepository
import org.flaxo.model.dao.CredentialsRepository
import org.flaxo.model.dao.PlagiarismReportRepository
import org.flaxo.model.dao.StudentRepository
import org.flaxo.model.dao.SolutionRepository
import org.flaxo.model.dao.TaskRepository
import org.flaxo.model.dao.UserRepository
import org.flaxo.model.data.BuildReport
import org.flaxo.model.data.CodeStyleReport
import org.flaxo.model.data.Course
import org.flaxo.model.data.CourseState
import org.flaxo.model.data.Credentials
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.PlagiarismReport
import org.flaxo.model.data.Student
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Task
import org.flaxo.model.data.User
import java.time.LocalDateTime

/**
 * Data service implementation based on jpa repositories.
 */
class BasicDataService(private val userRepository: UserRepository,
                       private val credentialsRepository: CredentialsRepository,
                       private val courseRepository: CourseRepository,
                       private val taskRepository: TaskRepository,
                       private val studentRepository: StudentRepository,
                       private val solutionRepository: SolutionRepository,
                       private val buildReportRepository: BuildReportRepository,
                       private val codeStyleReportRepository: CodeStyleReportRepository,
                       private val plagiarismReportRepository: PlagiarismReportRepository
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
                              description: String?,
                              language: String,
                              testingLanguage: String,
                              testingFramework: String,
                              tasksPrefix: String,
                              numberOfTasks: Int,
                              owner: User
    ): Course {
        if (getCourse(courseName, owner) != null)
            throw EntityAlreadyExistsException("Course $owner/$courseName")

        val course = courseRepository
                .save(Course(
                        name = courseName,
                        description = description,
                        url = "https://github.com/${owner.githubId}/$courseName",
                        createdDate = LocalDateTime.now(),
                        language = language,
                        testingLanguage = testingLanguage,
                        testingFramework = testingFramework,
                        state = CourseState(),
                        user = owner
                ))

        return (1..numberOfTasks)
                .map { taskNumber -> "$tasksPrefix$taskNumber"}
                .map { branchName ->
                    taskRepository
                            .save(Task(
                                    branch = branchName,
                                    url = "${course.url}/tree/$branchName",
                                    course = course
                            ))
                }
                .let { tasks ->
                    updateCourse(course.copy(tasks = course.tasks.plus(tasks)))
                }
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

        return taskRepository
                .findAllByCourse(course)
                .map { task -> solutionRepository.save(Solution(task = task, student = student)) }
                .let { solutions ->
                    studentRepository.save(student.copy(
                            solutions = student.solutions.plus(solutions)
                    ))
                }
                .also { updateCourse(course.copy(students = course.students.plus(it))) }
    }

    override fun getStudents(course: Course): Set<Student> =
            studentRepository.findByCourse(course)

    override fun getSolutions(student: Student): Set<Solution> =
            solutionRepository.findByStudent(student)

    override fun getSolutions(task: Task): Set<Solution> =
            solutionRepository.findByTask(task)

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

    override fun addBuildReport(solution: Solution,
                                succeed: Boolean
    ): BuildReport =
            buildReportRepository
                    .save(BuildReport(
                            solution = solution,
                            date = LocalDateTime.now(),
                            succeed = succeed
                    ))
                    .also {
                        updateSolution(solution.copy(
                                buildReports = solution.buildReports.plus(it)
                        ))
                    }

    override fun addCodeStyleReport(solution: Solution,
                                    codeStyleGrade: String
    ): CodeStyleReport =
            codeStyleReportRepository
                    .save(CodeStyleReport(
                            solution = solution,
                            date = LocalDateTime.now(),
                            grade = codeStyleGrade
                    ))
                    .also {
                        updateSolution(solution.copy(
                                codeStyleReports = solution.codeStyleReports.plus(it)
                        ))
                    }


    override fun addPlagiarismReport(task: Task,
                                     url: String,
                                     matches: List<PlagiarismMatch>
    ): PlagiarismReport =
            plagiarismReportRepository
                    .save(PlagiarismReport(
                            task = task,
                            date = LocalDateTime.now(),
                            url = url,
                            matches = matches
                    ))
                    .also {
                        updateTask(task.copy(
                                plagiarismReports = task.plagiarismReports.plus(it)
                        ))
                    }
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