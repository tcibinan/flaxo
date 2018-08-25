package org.flaxo.frontend.github

import org.flaxo.common.Course
import org.flaxo.common.Solution

fun githubProfileUrl(nickname: String): String = "https://github.com/$nickname"

fun githubPullRequestUrl(course: Course, solution: Solution): String? {
    val username = course.user.githubId
    val courseName = course.name
    return solution.commits.lastOrNull()
            ?.pullRequestId
            ?.let { pullRequest -> "https://github.com/$username/$courseName/pull/$pullRequest" }
}