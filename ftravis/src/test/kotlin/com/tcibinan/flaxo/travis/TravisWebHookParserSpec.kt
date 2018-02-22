package com.tcibinan.flaxo.travis

import com.tcibinan.flaxo.travis.build.BuildStatus
import com.tcibinan.flaxo.travis.build.TravisPullRequestBuild
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertTrue

object TravisWebHookParserSpec : Spek({

    val authorId = "user1"
    val branch = "task1"
    val courseRepositoryName = "courseRepo"
    val courseRepositoryOwner = "courseOwner"

    val webHookBody = """
        {
            "payload": {
                "status_message": "Passed",
                "type": "pull_request",
                "branch": "$branch",
                "author_name": "$authorId",
                "extra1": "extra property1",
                "repository": {
                    "extra2": "extra property2",
                    "name": "$courseRepositoryName",
                    "owner_name": "$courseRepositoryOwner"
                }
            }
        }
    """.trimIndent()

    describe("Travis web hook parsing function") {

        on("parsing web hook pull request") {
            val build = parseTravisWebHook(webHookBody.reader())
                    ?: throw Exception("Travis web hook wasn't parsed properly.")

            it("should parse web hook type properly") {
                assertTrue { build is TravisPullRequestBuild }
            }
        }

        on("parsing pull request web hook") {
            val build = parseTravisWebHook(webHookBody.reader())
                    as TravisPullRequestBuild?
                    ?: throw Exception("Travis pull request web hook wasn't parsed properly.")

            it("should parse pull request author") {
                assertTrue { build.authorId == authorId }
            }

            it("should parse pull request branch") {
                assertTrue { build.branch == branch }
            }

            it("should parse build status") {
                assertTrue { build.status == BuildStatus.SUCCEED }
            }

            it("should parse pull request receiver repository owner") {
                assertTrue { build.receiverId == courseRepositoryOwner }
            }

            it("should parse pull request receiver repository name") {
                assertTrue { build.receiverRepositoryName == courseRepositoryName }
            }
        }
    }
})