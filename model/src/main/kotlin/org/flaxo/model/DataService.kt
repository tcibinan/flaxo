package org.flaxo.model

import org.flaxo.model.data.BuildReport
import org.flaxo.model.data.CodeStyleReport
import org.flaxo.model.data.Course
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.PlagiarismReport
import org.flaxo.model.data.Student
import org.flaxo.model.data.Solution
import org.flaxo.model.data.Task
import org.flaxo.model.data.User

/**
 * Data service type for all model operations.
 */
interface DataService {

    /**
     * Adds new user.
     */
    fun addUser(nickname: String,
                password: String
    ): User

    /**
     * Retrieves user by nickname.
     */
    fun getUser(nickname: String): User?

    /**
     * Retrieves user by githubId.
     */
    fun getUserByGithubId(githubId: String): User?

    /**
     * Creates a course model with the provided parameters.
     *
     * Also create several tasks as well. Each task name contains [tasksPrefix]
     * and index. *f.e. task-1, task-2, ...*
     *
     * @param courseName git repository name.
     * @param description optional course description.
     * @param language main language of the course.
     * @param testingLanguage tests language.
     * @param testingFramework testing framework (f.e. junit / spek)
     * @param tasksPrefix prefix of each task of the course.
     * @param numberOfTasks amount of tasks in the course.
     * @param owner course author.
     * @return fully formed course with necessary amount of tasks.
     */
    fun createCourse(courseName: String,
                     description: String? = null,
                     language: String,
                     testingLanguage: String,
                     testingFramework: String,
                     tasksPrefix: String,
                     numberOfTasks: Int,
                     owner: User
    ): Course

    /**
     * Creates a course model with the provided parameters.
     *
     * Also create tasks with the given [tasksNames].
     *
     * @param courseName git repository name.
     * @param description optional course description.
     * @param language main language of the course.
     * @param testingLanguage tests language.
     * @param testingFramework testing framework (f.e. junit / spek)
     * @param tasksNames tasks name to be created.
     * @param owner course author.
     * @return fully formed course with necessary amount of tasks.
     */
    fun createCourse(courseName: String,
                     description: String? = null,
                     language: String,
                     testingLanguage: String,
                     testingFramework: String,
                     tasksNames: List<String>,
                     owner: User
    ): Course

    /**
     * Removes course model.
     */
    fun deleteCourse(courseName: String,
                     owner: User)

    /**
     * Changes existing course.
     */
    fun updateCourse(updatedCourse: Course): Course

    /**
     * Retrieves course by name and owner-user.
     */
    fun getCourse(name: String,
                  owner: User
    ): Course?

    /**
     * Retrieves all courses of the user with [userNickname].
     */
    fun getCourses(userNickname: String): Set<Course>

    /**
     * Adds student to a course.
     *
     * @param nickname git nickname.
     * @param course to be added student to.
     * @return student model.
     */
    fun addStudent(nickname: String,
                   course: Course
    ): Student

    /**
     * Retrieves all students of the course.
     */
    fun getStudents(course: Course): Set<Student>

    /**
     * Returns a set of student's solution.
     */
    fun getSolutions(student: Student): Set<Solution>

    /**
     * Returns a solutions set for a given task.
     */
    fun getSolutions(task: Task): Set<Solution>

    /**
     * Retrieves all tasks of the course.
     */
    fun getTasks(course: Course): Set<Task>

    /**
     * Adds service token to a user credentials.
     *
     * @param userNickname to be granted access token to.
     * @param service on which access token grants access.
     * @param accessToken the access token itself.
     */
    fun addToken(userNickname: String,
                 service: IntegratedService,
                 accessToken: String
    ): User

    /**
     * Adds [githubId] to a user with [userNickname].
     *
     * @return user with the given [userNickname] and added [githubId].
     */
    fun addGithubId(userNickname: String,
                    githubId: String
    ): User

    /**
     * Changes student task.
     *
     * @return changed tasks.
     */
    fun updateSolution(updatedSolution: Solution): Solution

    /**
     * Changes task.
     *
     * @return changed task.
     */
    fun updateTask(updatedTask: Task): Task

    /**
     * Adds new build report.
     */
    fun addBuildReport(solution: Solution,
                       succeed: Boolean
    ): BuildReport

    /**
     * Adds new code style report.
     */
    fun addCodeStyleReport(solution: Solution,
                           codeStyleGrade: String
    ): CodeStyleReport

    /**
     * Adds new plagiarism report.
     */
    fun addPlagiarismReport(task: Task,
                            url: String,
                            matches: List<PlagiarismMatch>
    ): PlagiarismReport

    /**
     * Deletes user.
     */
    fun deleteUser(username: String)

}
