package org.flaxo.github

import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.flaxo.common.env.file.StringEnvironmentFile
import org.flaxo.github.graphql.GithubQL
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import kotlin.random.Random
import org.kohsuke.github.GitHub as KohsukeGithub

// TODO 01.09.18: Move to an integration tests folder
object GithubSpec : SubjectSpek<Github>({

    val credentials = System.getenv("GITHUB_USER1_TOKEN")

    val repositoryName = "temp-testing-repository-" + Random.nextInt(1000)
    val mainBranchName = "main-branch"
    val subBranchName = "sub-branch"
    val fileName = "file-name"

    subject {
        Github(
                githubClientProducer = { KohsukeGithub.connectUsingOAuth(credentials) },
                rawWebHookUrl = "http://ignored.web.hook.url",
                githubQL = GithubQL.from(credentials)
        )
    }

    afterGroup {
        subject.getRepository(repositoryName)
                .delete()
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

            mainBranch.commit(StringEnvironmentFile(fileName, "file content"))

            it("should be loaded only in the targeted branch") {
                repository.branches()
                        .filter { it.name == mainBranchName }
                        .flatMap { it.files() }
                        .filter { it.fileName == fileName }
                        .count() shouldEqual 1
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
                        .filter { it.fileName == fileName }
                        .count() shouldEqual 1
            }
        }
    }
})
