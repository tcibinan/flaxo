package org.flaxo.github

import arrow.core.getOrHandle
import kotlinx.coroutines.experimental.runBlocking
import org.flaxo.git.AddReviewRequest
import org.flaxo.git.Branch
import org.flaxo.git.PullRequest
import org.flaxo.git.PullRequestReview
import org.flaxo.git.Repository
import java.io.IOException

data class GithubRepository(override val name: String,
                            override val owner: String,
                            private val github: Github
) : Repository {

    val client by lazy { github.client }
    val rawRepository by lazy { client.repository(owner, name) }

    override fun exists(): Boolean = try {
        client.repository(owner, name)
        true
    } catch (e: IOException) {
        false
    }

    override val forks: Int by lazy { rawRepository.forks }

    override fun delete() = rawRepository.delete()

    override fun branches(): List<Branch> =
            rawRepository
                    .branches
                    .values
                    .map { branch -> GithubBranch(branch.name, this, github) }

    override fun createBranch(branchName: String): Branch {
        val shA1 = rawRepository.listCommits().asList().last().shA1
        rawRepository.createBranch(branchName, shA1)

        return GithubBranch(branchName, this, github)
    }

    override fun addWebHook() {
        rawRepository.createWebHook(github.webHookUrl, listOf(RawGithubEvent.PULL_REQUEST))
    }

    override fun getPullRequest(pullRequestNumber: Int): PullRequest =
            rawRepository
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

    override fun addPullRequestReview(request: AddReviewRequest): PullRequestReview = runBlocking {
        github.githubQL.addReview(name, owner, request)
    }.getOrHandle { e ->
        throw GithubException("GitHub repository $owner/$name review addition failed: $request", e)
    }
}
