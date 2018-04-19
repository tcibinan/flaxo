package org.flaxo.github

import com.nhaarman.mockito_kotlin.mock
import org.flaxo.git.Git
import io.kotlintest.matchers.contain
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xon
import org.jetbrains.spek.subject.SubjectSpek

object GithubSpec : SubjectSpek<Git>({

    val userName = System.getenv("GITHUB_TEST_NAME")
    val credentials = System.getenv("GITHUB_TEST_TOKEN")

    val github: Git = mock { }
    val repository = GithubRepository("temp-testing-repository", userName, github)
    val mainBranch = GithubBranch("main-branch", repository, github)
    val subbranch = GithubBranch("sub-branch", repository, github)
    val anotherSubbranch = GithubBranch("another-sub-branch", repository, github)
    val fileName = "file-name"

    subject { Github(credentials, "http://example.com") }

    afterGroup {
        subject.deleteRepository(repository.name)
    }

    describe("github") {

        on("creating custom repository with single branch") {
            subject.createRepository(repository.name)
                    .createBranch(mainBranch.name)

            it("should create with a single branch") {
                subject.branches(userName, repository.name)
                        .map { it.name } shouldContain mainBranch.name
            }
        }

        on("creating subbranch") {
            subject.createSubBranch(repository, mainBranch, subbranch.name)

            it("should create the subbranch with the given name") {
                subject.branches(userName, repository.name)
                        .map { it.name } shouldContain subbranch.name
            }
        }

        xon("loading file in branch", "getting files list is not supported yet") {
            subject.load(repository, mainBranch, fileName, "file content")

            it("should exist only in that branch") {
                subject.branches(userName, repository.name)
                        .flatMap { it.files() }
                        .filter { it.name == fileName }
                        .count() shouldBe 1
            }
        }

        xon("creating subbranch", "getting files list is not supported yet") {
            subject.createSubBranch(repository, mainBranch, anotherSubbranch.name)

            it("should copy commits") {
                subject.branches(userName, repository.name)
                        .flatMap { it.files() }
                        .filter { it.name == fileName }
                        .count() shouldBe 2
            }
        }
    }
})

private infix fun <E> Collection<E>.shouldContain(element: E) {
    this shouldHave contain(element)
}