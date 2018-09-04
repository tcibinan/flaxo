package org.flaxo.github

import org.flaxo.git.Git
import org.flaxo.git.Repository
import java.net.URL

/**
 * Github client class.
 */
class Github(githubClientProducer: () -> RawGithub,
             rawWebHookUrl: String
) : Git {

    val webHookUrl: URL = URL(rawWebHookUrl)
    val client: RawGithub by lazy(githubClientProducer)

    override fun createRepository(repositoryName: String,
                                  private: Boolean
    ): Repository {
        client.createRepository(repositoryName)
                .private_(private)
                .create()
                .also {
                    it.createContent(
                            "# $repositoryName\n${flaxoMarkdownBadge()}\n",
                            "Initial commit from flaxo with love ♥",
                            "README.md"
                    )
                }

        return GithubRepository(repositoryName, nickname(), this)
    }

    private fun flaxoMarkdownBadge() = "[![from_flaxo with_♥]" +
            "(https://img.shields.io/badge/from_flaxo-with_♥-blue.svg)]" +
            "(https://github.com/tcibinan/flaxo)"

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
}