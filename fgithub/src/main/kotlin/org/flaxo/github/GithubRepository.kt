package org.flaxo.github

import org.flaxo.git.Branch
import org.flaxo.git.PullRequest
import org.flaxo.git.Repository
import org.kohsuke.github.GHEvent
import org.kohsuke.github.GHIssueState
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
                .createWebHook(github.webHookUrl, listOf(GHEvent.PULL_REQUEST))
    }

    override fun getPullRequest(pullRequestNumber: Int): PullRequest =
            client.repository(owner, name)
                    .getPullRequest(pullRequestNumber)
                    ?.let { GithubPullRequest(it) }
                    ?: throw GithubException("Pull request $pullRequestNumber wasn't found " +
                            "for repositoryName ${github.nickname()}/$name.")

    override fun getOpenPullRequests(): List<PullRequest> =
            client.repository(owner, name)
                    .getPullRequests(GHIssueState.OPEN)
                    .map { GithubPullRequest(it) }

}