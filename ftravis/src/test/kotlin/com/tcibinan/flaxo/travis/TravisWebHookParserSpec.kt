package com.tcibinan.flaxo.travis

import com.tcibinan.flaxo.travis.build.BuildStatus
import com.tcibinan.flaxo.travis.build.TravisPullRequestBuild
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertTrue

object TravisWebHookParserSpec : Spek({

    val pullRequestAuthor = "pullRequestAuthor"
    val branch = "somebranch"
    val repositoryName = "repositoryName"
    val repositoryOwner = "repositoryOwner"

    val webHookBody = """
        {
            "payload": {
                "status_message": "Passed",
                "type": "pull_request",
                "branch": "$branch",
                "author_name": "$pullRequestAuthor",
                "extra1": "extra property1",
                "repository": {
                    "extra2": "extra property2",
                    "name": "$repositoryName",
                    "owner_name": "$repositoryOwner"
                }
            }
        }
    """.trimIndent()

    describe("Travis web hook parsing function") {

        on("parsing web hook") {
            val build = parseTravisWebHook(webHookBody.reader())
                    ?: throw Exception("Travis web hook wasn't parsed properly.")

            it("should get web hook type") {
                assertTrue { build is TravisPullRequestBuild }
            }
        }

        on("parsing pull request web hook") {
            val build = parseTravisWebHook(webHookBody.reader())
                    as TravisPullRequestBuild?
                    ?: throw Exception("Travis pull request web hook wasn't parsed properly.")

            it("should get pull request author") {
                assertTrue { build.author == pullRequestAuthor }
            }

            it("should get pull request branch") {
                assertTrue { build.branch == branch }
            }

            it("should get build status") {
                assertTrue { build.status == BuildStatus.SUCCEED }
            }

            it("should get pull request repository owner") {
                assertTrue { build.repositoryOwner == repositoryOwner }
            }

            it("should get pull request repository name") {
                assertTrue { build.repositoryName == repositoryName }
            }
        }
    }
})