package com.tcibinan.flaxo.github

import com.tcibinan.flaxo.git.GitInstance
import io.kotlintest.matchers.contain
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldHave
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.api.dsl.xon
import org.jetbrains.spek.subject.SubjectSpek
import java.util.Properties

object GithubInstanceSpec : SubjectSpek<GitInstance>({

    val properties = Properties().apply {
        GithubInstanceSpec.javaClass.classLoader
                .getResourceAsStream("secured.properties")
                .use { load(it) }
    }

    val userName = properties.getProperty("github.username")
    val credentials = properties.getProperty("github.access.token")

    val repository = GithubRepository("temp-testing-repository", userName)
    val mainBranch = GithubBranch("main-branch", repository)
    val subbranch = GithubBranch("sub-branch", repository)
    val anotherSubbranch = GithubBranch("another-sub-branch", repository)
    val fileName = "file-name"

    subject { GithubInstance(credentials, "http://example.com") }

    afterGroup {
        subject.deleteRepository(repository.name())
    }

    describe("git service") {

        on("creating custom repository with single branch") {
            subject.createRepository(repository.name())
                    .createBranch(mainBranch.name())

            it("should create with a single branch") {
                subject.branches(userName, repository.name()) shouldContain mainBranch
            }
        }

        on("creating subbranch") {
            subject.createSubBranch(repository, mainBranch, subbranch.name())

            it("should create the subbranch with the given name") {
                subject.branches(userName, repository.name()) shouldContain subbranch
            }
        }

        xon("loading file in branch", "getting files list is not supported yet") {
            subject.load(repository, mainBranch, fileName, "file content")

            it("should exist only in that branch") {
                subject.branches(userName, repository.name())
                        .flatMap { it.files().toList() }
                        .filter { it.first == fileName }
                        .count() shouldBe 1
            }
        }

        xon("creating subbranch", "getting files list is not supported yet") {
            subject.createSubBranch(repository, mainBranch, anotherSubbranch.name())

            it("should copy commits") {
                subject.branches(userName, repository.name())
                        .flatMap { it.files().toList() }
                        .filter { it.first == fileName }
                        .count() shouldBe 2
            }
        }
    }
})

private infix fun <E> Collection<E>.shouldContain(element: E) {
    this shouldHave contain(element)
}