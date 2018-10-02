// todo: Replace with kotlinx.serialization features
package org.flaxo.common.interop

import org.flaxo.common.BuildReport
import org.flaxo.common.CodeStyleReport
import org.flaxo.common.CodeStyleGrade
import org.flaxo.common.Commit
import org.flaxo.common.Course
import org.flaxo.common.CourseLifecycle
import org.flaxo.common.CourseState
import org.flaxo.common.CourseStatistics
import org.flaxo.common.DateTime
import org.flaxo.common.ExternalService
import org.flaxo.common.GithubAuthData
import org.flaxo.common.Language
import org.flaxo.common.PlagiarismMatch
import org.flaxo.common.PlagiarismReport
import org.flaxo.common.Solution
import org.flaxo.common.Task
import org.flaxo.common.User
import kotlin.js.Json

fun languageFromDynamic(languageJson: dynamic): Language =
        Language(name = languageJson.name,
                compatibleTestingLanguages = (languageJson.compatibleTestingLanguages as Array<String>).toList(),
                compatibleTestingFrameworks = (languageJson.compatibleTestingFrameworks as Array<String>).toList())


fun courseFromDynamic(courseJson: dynamic): Course =
        Course(name = courseJson.name,
                state = courseStateFromDynamic(courseJson.state),
                language = courseJson.language,
                testingLanguage = courseJson.testingLanguage,
                testingFramework = courseJson.testingFramework,
                description = courseJson.description,
                createdDate = DateTime.fromDateTimeString(courseJson.createdDate as String),
                tasks = (courseJson.tasks as Array<String>).toList(),
                students = (courseJson.students as Array<String>).toList(),
                url = courseJson.url,
                user = userFromDynamic(courseJson.user))

fun userFromDynamic(userJson: dynamic): User {
    return User(
            githubId = userJson.githubId,
            nickname = userJson.nickname,
            isGithubAuthorized = userJson.githubAuthorized,
            isTravisAuthorized = userJson.travisAuthorized,
            isCodacyAuthorized = userJson.codacyAuthorized
    )
}

fun courseStateFromDynamic(courseStateJson: dynamic): CourseState {
    return CourseState(
            lifecycle = CourseLifecycle.valueOf(courseStateJson.lifecycle),
            activatedServices = (courseStateJson.activatedServices as Array<String>).toList()
                    .map { ExternalService.valueOf(it) }
    )
}

fun courseStatisticsFromDynamic(courseStatisticsJson: dynamic): CourseStatistics =
        CourseStatistics(
                tasks = (courseStatisticsJson.tasks as Array<dynamic>).toList()
                        .map { taskFromDynamic(it) }
        )

fun taskFromDynamic(taskJson: dynamic): Task =
        Task(
                branch = taskJson.branch,
                deadline = nullableDateFromDynamic(taskJson.deadline),
                plagiarismReports = (taskJson.plagiarismReports as Array<dynamic>).toList()
                        .map { plagiarismReportFromDynamic(it) },
                url = taskJson.url,
                solutions = (taskJson.solutions as Array<dynamic>).toList()
                        .map { solutionFromDynamic(it) }
        )

fun solutionFromDynamic(solutionJson: dynamic): Solution =
        Solution(
                task = solutionJson.task,
                student = solutionJson.student,
                score = solutionJson.score,
                commits = (solutionJson.commits as Array<dynamic>).toList()
                        .map { commitFromDynamic(it) },
                buildReports = (solutionJson.buildReports as Array<dynamic>).toList()
                        .map { buildReportFromDynamic(it) },
                codeStyleReports = (solutionJson.codeStyleReports as Array<dynamic>).toList()
                        .map { codeStyleReportFromDynamic(it) },
                approved = solutionJson.approved
        )

fun codeStyleReportFromDynamic(codeStyleReportJson: dynamic): CodeStyleReport =
        CodeStyleReport(
                grade = CodeStyleGrade.valueOf(codeStyleReportJson.grade),
                date = DateTime.fromDateTimeString(codeStyleReportJson.date as String)
        )

fun buildReportFromDynamic(buildReportJson: dynamic): BuildReport =
        BuildReport(
                succeed = buildReportJson.succeed,
                date = DateTime.fromDateTimeString(buildReportJson.date as String)
        )

fun commitFromDynamic(commitJson: dynamic): Commit =
        Commit(
                sha = commitJson.sha,
                pullRequestId = commitJson.pullRequestId,
                date = nullableDateFromDynamic(commitJson.date)
        )

fun plagiarismReportFromDynamic(plagiarismReportJson: dynamic): PlagiarismReport =
        PlagiarismReport(
                url = plagiarismReportJson.url,
                date = DateTime.fromDateTimeString(plagiarismReportJson.date as String),
                matches = (plagiarismReportJson.matches as Array<dynamic>).toList()
                        .map { plagiarismMatchFromDynamic(it) }
        )

fun plagiarismMatchFromDynamic(plagiarismMatchJson: dynamic): PlagiarismMatch =
        PlagiarismMatch(
                url = plagiarismMatchJson.url,
                student1 = plagiarismMatchJson.student1,
                student2 = plagiarismMatchJson.student2,
                lines = plagiarismMatchJson.lines,
                percentage = plagiarismMatchJson.percentage
        )

fun nullableDateFromDynamic(dateString: String?): DateTime? = dateString?.let { DateTime.fromDateTimeString(it) }

fun githubAuthDataFromDynamic(githubAuthDataJson: dynamic): GithubAuthData =
        GithubAuthData(
                redirectUrl = githubAuthDataJson.redirectUrl,
                requestParams = mapFromDynamic(githubAuthDataJson.requestParams)
        )

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun mapFromDynamic(mapJson: dynamic): Map<String, String> {
    return (mapJson as? Json)
            ?.let {
                Object.getOwnPropertyNames(it)
                        .map { propertyName -> propertyName to it[propertyName] as String }
                        .toMap()
            }
            ?: emptyMap()
}

internal external object Object {
    fun getOwnPropertyNames(obj: Any): Array<String>
}