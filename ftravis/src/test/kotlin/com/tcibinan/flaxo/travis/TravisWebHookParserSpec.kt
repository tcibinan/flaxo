package com.tcibinan.flaxo.travis

import com.tcibinan.flaxo.travis.build.BuildStatus
import com.tcibinan.flaxo.travis.build.TravisPullRequestBuild
import io.vavr.kotlin.Try
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertNull
import kotlin.test.assertTrue

object TravisWebHookParserSpec : Spek({

    val pullRequestNumber = 777
    val branch = "somebranch"
    val repositoryName = "repositoryName"
    val repositoryOwner = "repositoryOwner"

    val webHookBody = """
        {
            "status_message": "Passed",
            "type": "pull_request",
            "branch": "$branch",
            "pull_request_number": $pullRequestNumber,
            "extra1": "extra property1",
            "repository": {
                "extra2": "extra property2",
                "name": "$repositoryName",
                "owner_name": "$repositoryOwner"
            }
        }
    """.trimIndent()

    val nonPullRequestWebHookBody = """
        {
            "status_message": "Passed",
            "type": "unknown_type",
            "branch": "$branch",
            "extra1": "extra property1",
            "repository": {
                "extra2": "extra property2",
                "name": "$repositoryName",
                "owner_name": "$repositoryOwner"
            }
        }
    """.trimIndent()

    describe("Travis web hook parsing function") {

        on("parsing web hook") {
            val build = parseTravisWebHook(webHookBody.reader())
                    ?: throw TravisException("Travis web hook wasn't parsed properly.")

            it("should get web hook type") {
                assertTrue { build is TravisPullRequestBuild }
            }
        }

        on("parsing non-pull request web hook") {
            val build = Try { parseTravisWebHook(nonPullRequestWebHookBody.reader()) }

            it("should not fail with absence of pull_request_number field in raw travis web hook") {
                assertTrue { build.isSuccess }
            }

            it("should return on unknown web hook type") {
                assertNull(build.get())
            }
        }

        on("parsing pull request web hook") {
            val build = parseTravisWebHook(webHookBody.reader())
                    as TravisPullRequestBuild?
                    ?: throw TravisException("Travis pull request web hook wasn't parsed properly.")

            it("should get pull request github number") {
                assertTrue { build.number == pullRequestNumber }
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