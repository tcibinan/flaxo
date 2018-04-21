package org.flaxo.github

import com.nhaarman.mockito_kotlin.mock
import org.flaxo.git.Git
import io.kotlintest.matchers.contain
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.kohsuke.github.GitHub as KohsukeGithub

object GithubSpec : SubjectSpek<Git>({

    val userName = System.getenv("GITHUB_TEST_NAME")
    val credentials = System.getenv("GITHUB_TEST_TOKEN")

    val github: Git = mock { }
    val repository = GithubRepository("temp-testing-repository", userName, github)
    val mainBranch = GithubBranch("main-branch", repository, github)
    val anotherSubbranch = GithubBranch("sub-branch", repository, github)
    val fileName = "file-name"

    subject { Github({ KohsukeGithub.connectUsingOAuth(credentials) }, "http://ignored.web.hook.url") }

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

        on("loading file in the repository") {
            subject.load(repository, mainBranch, fileName, "file content")

            it("should be loaded only in the targeted branch") {
                subject.branches(userName, repository.name)
                        .filter { it.name == mainBranch.name }
                        .flatMap { it.files() }
                        .filter { it.name == fileName }
                        .count() shouldBe 1
            }
        }

        on("creating subbranch") {
            subject.createSubBranch(repository, mainBranch, anotherSubbranch.name)

            it("should copy files from a parent branch") {
                subject.branches(userName, repository.name)
                        .filter { it.name == anotherSubbranch.name }
                        .flatMap { it.files() }
                        .filter { it.name == fileName }
                        .count() shouldBe 1
            }
        }
    }
})

private infix fun <E> Collection<E>.shouldContain(element: E) {
    this shouldHave contain(element)
}