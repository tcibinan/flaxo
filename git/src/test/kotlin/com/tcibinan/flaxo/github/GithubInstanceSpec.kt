package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.GitInstance
import io.kotlintest.matchers.shouldBe
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object GithubInstanceSpec : SubjectSpek<GitInstance>({

    val userName = "some-user"
    val credentials = "some-credentials"
    val repositoryName = "some-repository"
    val branchName = "some-branch"
    val path1 = "first-path"
    val path2 = "second-path"
    val content1 = "first-content"
    val content2 = "second-content"

    subject { GithubInstance(credentials) }

    describe("git service") {

        on("creating custom repository with 6 branches") {
            subject
                    .createRepository(repositoryName)
                    .createBranch(branchName)
                    .load(path1, content1)
                    .load(path2, content2)
                    .createSubBranches(5)

            it("should create such a repository") {
                subject.branches(userName, repositoryName).count() shouldBe 6
            }
        }
    }
})
