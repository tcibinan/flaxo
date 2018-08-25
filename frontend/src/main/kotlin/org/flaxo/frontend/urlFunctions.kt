package org.flaxo.frontend

import org.flaxo.frontend.data.Course
import org.flaxo.frontend.data.Solution

fun githubProfileUrl(nickname: String): String = "https://github.com/$nickname"

fun githubPullRequestUrl(course: Course, solution: Solution): String? {
    val username = course.user.githubId
    val courseName = course.name
    return solution.commits.lastOrNull()
            ?.pullRequestId
            ?.let { pullRequest -> "https://github.com/$username/$courseName/pull/$pullRequest" }
}