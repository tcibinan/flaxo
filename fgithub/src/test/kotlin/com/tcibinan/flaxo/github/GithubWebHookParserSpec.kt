package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.PullRequest
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertTrue

object GithubWebHookParserSpec : Spek({

    val pullRequestAuthor = "pullRequestAuthor"
    val repositoryOwner = "repositoryOwner"
    val repositoryName = "repositoryName"

    val webHookBody = """
        {
            "action": "opened",
            "pull_request": {
                "user": {
                    "login": "$pullRequestAuthor"
                }
            },
            "repository": {
                "name": "$repositoryName",
                "owner": {
                    "login": "$repositoryOwner"
                }
            }
        }
    """.trimIndent()

    describe("Github web hook parser") {

        on("parsing web hook") {
            val payload = parseGithubEvent(webHookBody.reader(), "pull_request")
                    ?: throw GithubException("Github web hook wasn't parsed properly.")

            it("should get web hook type") {
                assertTrue { payload is PullRequest }
            }
        }

        on("parsing pull request web hook") {
            val payload = parseGithubEvent(webHookBody.reader(), "pull_request")
                    as GithubPullRequest?
                    ?: throw GithubException("Github web hook wasn't parsed properly.")

            it("should get pull request state") {
                assertTrue { payload.isOpened }
            }

            it("should get pull request author") {
                assertTrue { payload.authorId == pullRequestAuthor }
            }

            it("should get pull request repository owner") {
                assertTrue { payload.receiverId == repositoryOwner }
            }

            it("should get pull request repository name") {
                assertTrue { payload.receiverRepositoryName == repositoryName }
            }
        }
    }
})