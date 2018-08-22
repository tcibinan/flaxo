package org.flaxo.travis

import arrow.core.Try
import arrow.core.getOrElse
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TravisWebHookParserSpec : Spek({

    val pullRequestNumber = 777
    val branch = "somebranch"
    val repositoryName = "repositoryName"
    val repositoryOwner = "repositoryOwner"
    val buildFinishedDate = "2018-05-14T06:47:09Z"
    val commitSha = "commitSha"

    val webHookBody = """
        {
            "status_message": "Passed",
            "type": "pull_request",
            "branch": "$branch",
            "finished_at": "$buildFinishedDate",
            "pull_request_number": $pullRequestNumber,
            "extra1": "extra property1",
            "repository": {
                "extra2": "extra property2",
                "name": "$repositoryName",
                "owner_name": "$repositoryOwner"
            },
            "commit": "$commitSha"
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
                build shouldBeInstanceOf TravisPullRequestBuild::class
            }

            it("should get web hook finished date") {
                build.finishedAt shouldEqual LocalDateTime.parse(
                        buildFinishedDate,
                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                )
            }

            it("should get web hook commit sha") {
                build.commitSha shouldEqual commitSha
            }
        }
    }

    on("parsing non-pull request web hook") {
        val build = Try { parseTravisWebHook(nonPullRequestWebHookBody.reader()) }

        it("should not fail with absence of pull_request_number field in raw travis web hook") {
            build.isSuccess().shouldBeTrue()
        }

        it("should return unknown web hook type") {
            build.map { it.shouldBeNull() }
        }
    }

    on("parsing pull request web hook") {
        val build = parseTravisWebHook(webHookBody.reader())
                as TravisPullRequestBuild?
                ?: throw TravisException("Travis pull request web hook wasn't parsed properly.")

        it("should get pull request github number") {
            build.pullRequestNumber shouldEqual pullRequestNumber
        }

        it("should get pull request branch") {
            build.branch shouldEqual branch
        }

        it("should get build status") {
            build.buildStatus shouldEqual TravisBuildStatus.SUCCEED
        }

        it("should get pull request repository owner") {
            build.repositoryOwner shouldEqual repositoryOwner
        }

        it("should get pull request repository name") {
            build.repositoryName shouldEqual repositoryName
        }
    }
})