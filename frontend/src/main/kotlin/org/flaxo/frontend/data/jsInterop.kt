// todo: Replace with kotlinx.serialization features
package org.flaxo.frontend.data

import kotlinext.js.Object.getOwnPropertyNames
import kotlin.js.Date
import kotlin.js.Json

fun languageFromDynamic(languageJson: dynamic) =
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
                createdDate = Date(courseJson.createdDate as String),
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
                        .map { codeStyleReportFromDynamic(it) }
        )

fun codeStyleReportFromDynamic(codeStyleReportJson: dynamic): CodeStyleReport =
        CodeStyleReport(
                grade = codeStyleReportJson.grade,
                date = Date(codeStyleReportJson.date as String)
        )

fun buildReportFromDynamic(buildReportJson: dynamic): BuildReport =
        BuildReport(
                succeed = buildReportJson.succeed,
                date = Date(buildReportJson.date as String)
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
                date = Date(plagiarismReportJson.date as String),
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

fun nullableDateFromDynamic(dateString: String?): Date? = dateString?.let { Date(it) }

fun githubAuthDataFromDynamic(githubAuthDataJson: dynamic): GithubAuthData =
        GithubAuthData(
                redirectUrl = githubAuthDataJson.redirectUrl,
                requestParams = mapFromDynamic(githubAuthDataJson.requestParams)
        )

@Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
fun mapFromDynamic(mapJson: dynamic): Map<String, String> {
    return (mapJson as? Json)
            ?.let {
                getOwnPropertyNames(it)
                        .map { propertyName -> propertyName to it[propertyName] as String }
                        .toMap()
            }
            ?: emptyMap()
}