package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.model.data.Student
import com.tcibinan.flaxo.model.data.Solution
import com.tcibinan.flaxo.model.data.Task
import com.tcibinan.flaxo.model.data.User

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
     * @param language main language of the course.
     * @param testingLanguage tests language.
     * @param testingFramework testing framework (f.e. junit / spek)
     * @param tasksPrefix prefix of each task of the course.
     * @param numberOfTasks amount of tasks in the course.
     * @param owner course author.
     * @return fully formed course with necessary amount of tasks.
     */
    fun createCourse(courseName: String,
                     language: String,
                     testingLanguage: String,
                     testingFramework: String,
                     tasksPrefix: String,
                     numberOfTasks: Int,
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
    fun updateStudentTask(updatedSolution: Solution): Solution

    /**
     * Changes task.
     *
     * @return changed task.
     */
    fun updateTask(updatedTask: Task): Task
}
