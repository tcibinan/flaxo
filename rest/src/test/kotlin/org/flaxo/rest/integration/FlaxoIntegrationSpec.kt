package org.flaxo.rest.integration

import io.kotlintest.matchers.containsAll
import io.kotlintest.matchers.should
import io.kotlintest.matchers.shouldBe
import org.flaxo.core.env.LocalEnvironmentFile
import org.flaxo.git.Git
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.ModelException
import org.flaxo.rest.Application
import org.flaxo.rest.api.CodacyController
import org.flaxo.rest.api.ModelController
import org.flaxo.rest.service.data.UserDetailsImpl
import org.flaxo.rest.service.git.GitService
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.core.env.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.security.Principal
import java.util.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

object FlaxoIntegrationSpec : Spek({

    val githubApi = "https://api.github.com"
    val codacyApi = "https://api.codacy.com/2.0"
    val travisApi = "https://api.travis-ci.org"
    val username = "test"
    val password = "test"
    val courseName = "integration-test-course-" + Random().nextInt().let { Math.abs(it) }
    val tasksFiles = mapOf(
            "task-1" to mapOf(
                    "src/main/java/org/flaxo/examples/Range.java"
                            to LocalEnvironmentFile("src/test/resources/tasks/1/Range.java"),
                    "src/test/kotlin/org/flaxo/examples/RangeSpec.kt"
                            to LocalEnvironmentFile("src/test/resources/tasks/1/RangeSpec.kt")
            ),
            "task-2" to mapOf(
                    "src/main/java/org/flaxo/examples/Traversable.java"
                            to LocalEnvironmentFile("src/test/resources/tasks/2/Traversable.java"),
                    "src/test/kotlin/org/flaxo/examples/TraversableSpec.kt"
                            to LocalEnvironmentFile("src/test/resources/tasks/2/TraversableSpec.kt")
            )
    )

    val context = SpringApplication.run(Application::class.java)
    val dataService = context.getBean<DataService>("dataService")
    val principal: () -> Principal = {
        val user = dataService.getUser(username)
                ?: throw ModelException("User not found")
        val userDetails = UserDetailsImpl(user)
        val authenticationToken =
                UsernamePasswordAuthenticationToken(
                        username,
                        password,
                        AuthorityUtils.createAuthorityList("USER")
                )
        authenticationToken
                .details = userDetails
        SecurityContextHolder.getContext()
                .authentication = authenticationToken

        authenticationToken
    }
    val mockMvc = MockMvcBuilders
            .standaloneSetup(
                    context.getBean<ModelController>(),
                    context.getBean<CodacyController>()
            )
            .build()
    val githubId = context.environment["GITHUB_TEST_NAME"]
    val githubToken = context.environment["GITHUB_TEST_TOKEN"]
    val codacyToken = context.environment["CODACY_TEST_TOKEN"]
    val travisToken = context.environment["TRAVIS_TEST_TOKEN"]
    val firstStudentToken = context.environment["GITHUB_USER2_TOKEN"]
    val secondStudentToken = context.environment["GITHUB_USER3_TOKEN"]
    val solutionsFiles = mapOf(
            firstStudentToken to mapOf(
                    "task-1" to mapOf(
                            "src/main/java/org/flaxo/examples/Range.java"
                                    to LocalEnvironmentFile("src/test/resources/solutions1/1/Range.java")
                    ),
                    "task-2" to mapOf(
                            "src/main/java/org/flaxo/examples/Traversable.java"
                                    to LocalEnvironmentFile("src/test/resources/solutions1/2/Traversable.java")
                    )
            )
    )
    val git: (String) -> Git = { context.getBean<GitService>().with(it) }

    afterGroup {
        context.use {
            dataService
                    .getUser(username)
                    ?.also { user ->
                        dataService
                                .getCourse(courseName, user)
                                ?.also {
                                    mockMvc.perform(post("/rest/deleteCourse")
                                            .principal(principal())
                                            .param("courseName", courseName)
                                    )
                                }

                        dataService.deleteUser(user.nickname)
                    }

            solutionsFiles.keys
                    .mapNotNull { token ->
                        try {
                            git(token).deleteRepository(courseName)
                            null
                        } catch (e: Throwable) {
                            e
                        }
                    }
                    .takeUnless { it.isEmpty() }
                    ?.also { throw it.first() }
        }
    }

    describe("flaxo course creation scenario") {

        on("registering user") {
            mockMvc.perform(post("/rest/register")
                    .param("nickname", username)
                    .param("password", password)
            )

            it("should create user account with given username and password") {
                val user = dataService.getUser(username)
                        ?: throw ModelException("User not found")

                user.nickname shouldBe username
                user.credentials.password shouldBe password
            }
        }

        on("addition user github data") {
            dataService.addGithubId(username, githubId)
            dataService.addToken(username, IntegratedService.GITHUB, githubToken)

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            it("should add user github id") {
                user.githubId shouldBe githubId
            }

            it("should add user github token") {
                user.credentials.githubToken shouldBe githubToken
            }
        }

        on("adding user codacy data") {
            mockMvc.perform(put("/rest/codacy/token")
                    .principal(principal())
                    .param("token", codacyToken)
            )

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            it("should add user codacy token") {
                user.credentials.codacyToken shouldBe codacyToken
            }
        }

        on("creating course") {
            mockMvc.perform(post("/rest/createCourse")
                    .principal(principal())
                    .param("courseName", courseName)
                    .param("language", "java")
                    .param("testingLanguage", "kotlin")
                    .param("testingFramework", "spek")
                    .param("numberOfTasks", "2")
            )

            it("should create a git repository") {
                khttp.get("$githubApi/repos/$githubId/$courseName")
                        .statusCode shouldBe 200
            }
        }

        on("pushing tasks") {
            git(githubToken)
                    .getRepository(courseName)
                    .branches()
                    .forEach { branch ->
                        tasksFiles[branch.name]
                                ?.forEach { filePath, file ->
                                    branch.commit(filePath, file)
                                }
                    }

            it("should add new files to tasks branches") {
                git(githubToken)
                        .getRepository(courseName)
                        .branches()
                        .forEach { branch ->
                            tasksFiles[branch.name]
                                    ?.keys
                                    ?.also { fileNames ->
                                        branch.files()
                                                .map { it.name } should containsAll(fileNames.toList())
                                    }
                        }
            }
        }

        on("starting course") {
            mockMvc.perform(post("/rest/composeCourse")
                    .principal(principal())
                    .param("courseName", courseName)
            )

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            it("should activate codacy validations") {
                khttp
                        .get("$codacyApi/$githubId/$courseName",
                                headers = mapOf("api_token" to codacyToken)
                        )
                        .statusCode shouldBe 200
            }

            it("should retrieve travis token") {
                user.credentials.travisToken
                        ?: throw ModelException("Travis token is not set")
            }

            it("should activate travis validations") {
                khttp
                        .get("$travisApi/repo/$githubId%2F$courseName",
                                headers = mapOf(
                                        "Authorization" to "token $travisToken",
                                        "Travis-API-Version" to "3"
                                )
                        )
                        .statusCode shouldBe 200
            }
        }

        on("pushing solutions") {
            solutionsFiles.forEach { token, solutions ->
                git(token)
                        .forkRepository(githubId, courseName)
                        .branches()
                        .forEach { branch ->
                            solutions[branch.name]
                                    ?.forEach { (filePath, file) ->
                                        branch.update(filePath, file)
                                    }
                        }
            }

            it("should fork course repository for each student") {
                git(githubToken)
                        .getRepository(courseName)
                        .forks shouldBe solutionsFiles.size
            }
        }
    }
})