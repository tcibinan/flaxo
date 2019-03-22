// todo: Replace with kotlinx.serialization features
package org.flaxo.common.interop

import org.flaxo.common.DateTime
import org.flaxo.common.data.BuildReport
import org.flaxo.common.data.CodeStyleGrade
import org.flaxo.common.data.CodeStyleReport
import org.flaxo.common.data.Commit
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseLifecycle
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.CourseState
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.ExternalService
import org.flaxo.common.data.GithubAuthData
import org.flaxo.common.data.Language
import org.flaxo.common.data.PlagiarismMatch
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.Solution
import org.flaxo.common.data.Task
import org.flaxo.common.data.User
import kotlin.js.Json

private const val FALLBACK_DATE = "1999-31-12T23:59:59.000"

/**
 * Converts a JS dynamic object to language.
 */
fun languageFromDynamic(languageJson: dynamic): Language =
        Language(name = languageJson.name,
                compatibleTestingLanguages = (languageJson.compatibleTestingLanguages as? Array<String>
                        ?: emptyArray()).toList(),
                compatibleTestingFrameworks = (languageJson.compatibleTestingFrameworks as? Array<String>
                        ?: emptyArray()).toList())

/**
 * Converts a JS dynamic object to course.
 */
fun courseFromDynamic(courseJson: dynamic): Course =
        Course(id=courseJson.id,
                name = courseJson.name,
                state = courseStateFromDynamic(courseJson.state),
                settings = CourseSettings(
                        id = courseJson.settings.id,
                        language = courseJson.settings.language,
                        testingLanguage = courseJson.settings.testingLanguage,
                        testingFramework = courseJson.settings.testingFramework
                ),
                description = courseJson.description,
                date = DateTime.fromDateTimeString(courseJson.date as? String ?: FALLBACK_DATE),
                tasks = (courseJson.tasks as Array<String>).toList(),
                students = (courseJson.students as Array<String>).toList(),
                url = courseJson.url,
                user = userFromDynamic(courseJson.user))

/**
 * Converts a JS dynamic object to user.
 */
fun userFromDynamic(userJson: dynamic): User =
        User(id = userJson.id,
                githubId = userJson.githubId,
                name = userJson.name,
                date = DateTime.fromDateTimeString(userJson.date as? String ?: FALLBACK_DATE),
                isGithubAuthorized = userJson.githubAuthorized,
                isTravisAuthorized = userJson.travisAuthorized,
                isCodacyAuthorized = userJson.codacyAuthorized)

/**
 * Converts a JS dynamic object to course state.
 */
fun courseStateFromDynamic(courseStateJson: dynamic): CourseState {
    return CourseState(
            id = courseStateJson.id,
            lifecycle = CourseLifecycle.valueOf(courseStateJson.lifecycle),
            activatedServices = (courseStateJson.activatedServices as? Array<String> ?: emptyArray())
                    .toList()
                    .map { ExternalService.valueOf(it) }
    )
}

/**
 * Converts a JS dynamic object to course statistics.
 */
fun courseStatisticsFromDynamic(courseStatisticsJson: dynamic): CourseStatistics =
        CourseStatistics(
                tasks = (courseStatisticsJson.tasks as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { taskFromDynamic(it) }
        )

/**
 * Converts a JS dynamic object to task.
 */
fun taskFromDynamic(taskJson: dynamic): Task =
        Task(
                id = taskJson.id,
                name = taskJson.name,
                branch = taskJson.branch,
                deadline = nullableDateFromDynamic(taskJson.deadline),
                plagiarismReports = (taskJson.plagiarismReports as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { plagiarismReportFromDynamic(it) },
                url = taskJson.url,
                solutions = (taskJson.solutions as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { solutionFromDynamic(it) }
        )

/**
 * Converts a JS dynamic object to solution.
 */
fun solutionFromDynamic(solutionJson: dynamic): Solution =
        Solution(
                id = solutionJson.id,
                task = solutionJson.task,
                student = solutionJson.student,
                score = solutionJson.score,
                commits = (solutionJson.commits as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { commitFromDynamic(it) },
                buildReports = (solutionJson.buildReports as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { buildReportFromDynamic(it) },
                codeStyleReports = (solutionJson.codeStyleReports as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { codeStyleReportFromDynamic(it) },
                approved = solutionJson.approved
        )

/**
 * Converts a JS dynamic object to code style report.
 */
fun codeStyleReportFromDynamic(codeStyleReportJson: dynamic): CodeStyleReport =
        CodeStyleReport(
                id = codeStyleReportJson.id,
                grade = CodeStyleGrade.valueOf(codeStyleReportJson.grade),
                date = DateTime.fromDateTimeString(codeStyleReportJson.date as String)
        )

/**
 * Converts a JS dynamic object to build report.
 */
fun buildReportFromDynamic(buildReportJson: dynamic): BuildReport =
        BuildReport(
                id = buildReportJson.id,
                succeed = buildReportJson.succeed,
                date = DateTime.fromDateTimeString(buildReportJson.date as? String ?: FALLBACK_DATE)
        )

/**
 * Converts a JS dynamic object to commit.
 */
fun commitFromDynamic(commitJson: dynamic): Commit =
        Commit(
                id = commitJson.id,
                sha = commitJson.sha,
                pullRequestId = commitJson.pullRequestId,
                date = nullableDateFromDynamic(commitJson.date)
        )

/**
 * Converts a JS dynamic object to plagiarism report.
 */
fun plagiarismReportFromDynamic(plagiarismReportJson: dynamic): PlagiarismReport =
        PlagiarismReport(
                id = plagiarismReportJson.id,
                url = plagiarismReportJson.url,
                date = DateTime.fromDateTimeString(plagiarismReportJson.date as? String ?: FALLBACK_DATE),
                matches = (plagiarismReportJson.matches as? Array<dynamic> ?: emptyArray())
                        .toList()
                        .map { plagiarismMatchFromDynamic(it) }
        )

/**
 * Converts a JS dynamic object to plagiarism match.
 */
fun plagiarismMatchFromDynamic(plagiarismMatchJson: dynamic): PlagiarismMatch =
        PlagiarismMatch(
                id = plagiarismMatchJson.id,
                url = plagiarismMatchJson.url,
                student1 = plagiarismMatchJson.student1,
                student2 = plagiarismMatchJson.student2,
                lines = plagiarismMatchJson.lines,
                percentage = plagiarismMatchJson.percentage
        )

/**
 * Converts a date time string to a date time instance.
 */
fun nullableDateFromDynamic(dateString: String?): DateTime? = dateString?.let { DateTime.fromDateTimeString(it) }

/**
 * Converts a JS dynamic object to github auth data.
 */
fun githubAuthDataFromDynamic(githubAuthDataJson: dynamic): GithubAuthData =
        GithubAuthData(
                redirectUrl = githubAuthDataJson.redirectUrl,
                requestParams = mapFromDynamic(githubAuthDataJson.requestParams)
        )

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
private fun mapFromDynamic(mapJson: dynamic): Map<String, String> =
        (mapJson as? Json)
                ?.let {
                    Object.getOwnPropertyNames(it)
                            .map { propertyName -> propertyName to it[propertyName] as String }
                            .toMap()
                }
                ?: emptyMap()

internal external object Object {
    fun getOwnPropertyNames(obj: Any): Array<String>
}
