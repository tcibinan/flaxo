package org.flaxo.github

import arrow.core.getOrHandle
import kotlinx.coroutines.experimental.runBlocking
import org.flaxo.git.Branch
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview
import org.flaxo.git.Repository
import java.io.IOException

data class GithubRepository(override val name: String,
                            override val owner: String,
                            private val github: Github
) : Repository {

    override fun exists(): Boolean =
            try {
                client.repository(owner, name)
                true
            } catch (e: IOException) {
                false
            }

    private val client = github.client

    override val forks: Int = client.repository(owner, name).forks

    override fun delete() {
        client.repository(owner, name).delete()
    }

    override fun branches(): List<Branch> =
            client.repository(owner, name)
                    .branches
                    .values
                    .map { branch ->
                        GithubBranch(branch.name, this, github)
                    }

    override fun createBranch(branchName: String): Branch {
        client.repository(owner, name).apply {
            listCommits().asList().last().shA1.also {
                createBranch(branchName, it)
            }
        }

        return GithubBranch(branchName, this, github)
    }

    override fun addWebHook() {
        client.repository(owner, name)
                .createWebHook(github.webHookUrl, listOf(RawGithubEvent.PULL_REQUEST))
    }

    override fun getPullRequest(pullRequestNumber: Int): PullRequest =
            client.repository(owner, name)
                    .getPullRequest(pullRequestNumber)
                    ?.let { GithubPullRequest(it) }
                    ?: throw GithubException("Pull request $pullRequestNumber wasn't found " +
                            "for repositoryName ${github.nickname()}/$name.")

    override fun getPullRequests(): List<PullRequest> = runBlocking {
        github.githubQL.pullRequests(name, owner)
    }.getOrHandle { e ->
        throw GithubException("GitHub repository $owner/$name pull requests retrieving failed", e)
    }

    override fun getPullRequestReviews(pullRequestNumber: Int): List<PullRequestReview> = runBlocking {
        github.githubQL.reviews(name, owner, pullRequestNumber)
    }.getOrHandle { e ->
        throw GithubException("GitHub repository $owner/$name pull request #$pullRequestNumber reviews " +
                "retrieving failed", e)
    }

}
