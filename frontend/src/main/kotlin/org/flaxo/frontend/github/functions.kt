package org.flaxo.frontend.github

import org.flaxo.common.data.Course
import org.flaxo.common.data.Solution

/**
 * Returns a github profile url based on user [nickname].
 */
fun githubProfileUrl(nickname: String): String = "https://github.com/$nickname"

/**
 * Returns a pull request url based on solution [course] and [solution].
 */
fun githubPullRequestUrl(course: Course, solution: Solution): String? {
    val username = course.user.githubId
    val courseName = course.name
    return solution.commits.lastOrNull()
            ?.pullRequestId
            ?.let { pullRequest -> "https://github.com/$username/$courseName/pull/$pullRequest" }
}
