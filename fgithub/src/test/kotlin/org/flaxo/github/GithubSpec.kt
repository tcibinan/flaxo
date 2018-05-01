package org.flaxo.github

import io.kotlintest.matchers.contain
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import org.flaxo.core.env.SimpleEnvironmentFile
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.kohsuke.github.GitHub as KohsukeGithub

object GithubSpec : SubjectSpek<Github>({

    val credentials = System.getenv("GITHUB_TEST_TOKEN")

    val repositoryName = "temp-testing-repository"
    val mainBranchName = "main-branch"
    val subBranchName = "sub-branch"
    val fileName = "file-name"

    subject { Github({ KohsukeGithub.connectUsingOAuth(credentials) }, "http://ignored.web.hook.url") }

    afterGroup {
        subject.deleteRepository(repositoryName)
    }

    describe("github") {

        on("creating custom repository with a single branch") {
            val repository = subject.createRepository(repositoryName)

            repository.createBranch(mainBranchName)

            it("should create with a single branch") {
                repository.branches()
                        .map { it.name } shouldContain mainBranchName
            }
        }

        on("loading file in the repository") {
            val repository = subject.getRepository(repositoryName)
            val mainBranch = repository
                    .branches()
                    .find { it.name == mainBranchName }
                    ?: throw GithubException("Branch not found")

            mainBranch.commit(SimpleEnvironmentFile(fileName, "file content"))

            it("should be loaded only in the targeted branch") {
                repository.branches()
                        .filter { it.name == mainBranchName }
                        .flatMap { it.files() }
                        .filter { it.name == fileName }
                        .count() shouldBe 1
            }
        }

        on("creating subbranch") {
            val repository = subject.getRepository(repositoryName)
            val mainBranch = repository
                    .branches()
                    .find { it.name == mainBranchName }
                    ?: throw GithubException("Branch not found")

            mainBranch.createSubBranch(subBranchName)

            it("should copy files from a parent branch") {
                repository.branches()
                        .filter { it.name == subBranchName }
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