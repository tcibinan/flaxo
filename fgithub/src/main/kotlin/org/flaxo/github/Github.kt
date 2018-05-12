package org.flaxo.github

import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.env.RemoteEnvironmentFile
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.git.PullRequest
import org.flaxo.git.Repository
import java.net.URL
import org.kohsuke.github.GHEvent as KohsukeGithubEvent
import org.kohsuke.github.GitHub as KohsukeGithub

/**
 * Github client class.
 */
class Github(private val githubClientProducer: () -> KohsukeGithub,
             rawWebHookUrl: String
) : Git {

    val webHookUrl: URL = URL(rawWebHookUrl)
    val client: KohsukeGithub by lazy { githubClientProducer() }

    override fun createRepository(repositoryName: String,
                                  private: Boolean
    ): Repository {
        client.createRepository(repositoryName)
                .private_(private)
                .create()
                .also {
                    it.createContent(
                            "# $repositoryName",
                            "Initial commit from flaxo with love ♥",
                            "README.md"
                    )
                }

        return GithubRepository(repositoryName, nickname(), this)
    }

    override fun deleteRepository(repositoryName: String) {
        client.repository(repositoryName).delete()
    }

    override fun forkRepository(ownerNickname: String,
                                repositoryName: String
    ): Repository =
            client.getUser(ownerNickname)
                    .getRepository(repositoryName)
                    ?.fork()
                    ?.let { GithubRepository(repositoryName, nickname(), this) }
                    ?: throw GithubException("Repository $ownerNickname/$repositoryName was not found")

    override fun getRepository(repositoryName: String): Repository =
            GithubRepository(repositoryName, nickname(), this)

    override fun getRepository(ownerName: String,
                               repositoryName: String
    ): Repository =
            GithubRepository(repositoryName, ownerName, this)

    override fun nickname(): String = client.nickname()

    override fun getPullRequest(repositoryName: String,
                                pullRequestNumber: Int
    ): PullRequest =
            client.repository(repositoryName)
                    .getPullRequest(pullRequestNumber)
                    ?.let { GithubPullRequest(it) }
                    ?: throw GithubException("Pull request $pullRequestNumber wasn't found " +
                            "for repositoryName ${nickname()}/$repositoryName.")
}