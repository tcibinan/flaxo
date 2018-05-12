package org.flaxo.rest.integration

import org.amshove.kluent.shouldContainAll
import org.amshove.kluent.shouldEqual
import org.flaxo.core.env.LocalEnvironmentFile
import org.flaxo.core.of
import org.flaxo.core.repeatUntil
import org.flaxo.git.Commit
import org.flaxo.git.Git
import org.flaxo.github.GithubException
import org.flaxo.model.CourseLifecycle
import org.flaxo.model.DataService
import org.flaxo.model.IntegratedService
import org.flaxo.model.ModelException
import org.flaxo.rest.Application
import org.flaxo.rest.api.CodacyController
import org.flaxo.rest.api.CourseController
import org.flaxo.rest.api.UserController
import org.flaxo.rest.service.codacy.CodacyService
import org.flaxo.rest.service.data.UserDetailsImpl
import org.flaxo.rest.service.git.GitService
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.json.JSONObject
import org.springframework.beans.factory.getBean
import org.springframework.boot.SpringApplication
import org.springframework.core.env.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.security.Principal
import java.util.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import java.util.concurrent.TimeUnit

object FlaxoIntegrationSpec : Spek({

    val githubApi = "https://api.github.com"
    val codacyApi = "https://api.codacy.com/2.0"
    val travisApi = "https://api.travis-ci.org"
    val username = "test"
    val password = "test"
    val courseName = "integration-test-course-" + Random().nextInt().let { Math.abs(it) }
    val teacherFiles = mapOf(
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
    val codacyService = context.getBean<CodacyService>()
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
                    context.getBean<UserController>(),
                    context.getBean<CourseController>(),
                    context.getBean<CodacyController>()
            )
            .build()
    val githubId = context.environment["GITHUB_USER1_NAME"]
    val githubToken = context.environment["GITHUB_USER1_TOKEN"]
    val codacyToken = context.environment["CODACY_USER1_TOKEN"]
    val travisToken = context.environment["TRAVIS_USER1_TOKEN"]
    val firstStudentToken = context.environment["GITHUB_USER2_TOKEN"]
    val secondStudentToken = context.environment["GITHUB_USER3_TOKEN"]
    val firstStudentFiles = mapOf(
            "task-1" to mapOf(
                    "src/main/java/org/flaxo/examples/Range.java"
                            to LocalEnvironmentFile("src/test/resources/solutions1/1/Range.java")
            ),
            "task-2" to mapOf(
                    "src/main/java/org/flaxo/examples/Traversable.java"
                            to LocalEnvironmentFile("src/test/resources/solutions1/2/Traversable.java")
            )
    )
    val gitTokenToFiles = mapOf(
            firstStudentToken to firstStudentFiles
    )
    val solutionsCommits = mutableListOf<Commit>()
    val git: (String) -> Git = { context.getBean<GitService>().with(it) }

    afterGroup {
        context.use {
            dataService
                    .getUser(username)
                    ?.also { user ->
                        dataService
                                .getCourse(courseName, user)
                                ?.also {
                                    mockMvc.perform(delete("/rest/course/delete")
                                            .principal(principal())
                                            .param("courseName", courseName)
                                    )
                                }

                        dataService.deleteUser(user.nickname)
                    }

            gitTokenToFiles.keys
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
            mockMvc.perform(post("/rest/user/register")
                    .param("nickname", username)
                    .param("password", password)
            )

            it("should create user account with given username") {
                val user = dataService.getUser(username)
                        ?: throw ModelException("User not found")

                user.nickname shouldEqual username
            }
        }

        on("addition user github data") {
            dataService.addGithubId(username, githubId)
            dataService.addToken(username, IntegratedService.GITHUB, githubToken)

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            it("should add user github id") {
                user.githubId shouldEqual githubId
            }

            it("should add user github token") {
                user.credentials.githubToken shouldEqual githubToken
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
                user.credentials.codacyToken shouldEqual codacyToken
            }
        }

        on("adding user travis data") {
            dataService.addToken(username, IntegratedService.TRAVIS, travisToken)

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            it("should add user travis token") {
                user.credentials.travisToken shouldEqual travisToken
            }
        }

        on("creating course") {
            mockMvc.perform(post("/rest/course/create")
                    .principal(principal())
                    .param("courseName", courseName)
                    .param("language", "java")
                    .param("testingLanguage", "kotlin")
                    .param("testingFramework", "spek")
                    .param("numberOfTasks", "2")
            )

            it("should create a git repository") {
                khttp.get("$githubApi/repos/$githubId/$courseName")
                        .statusCode shouldEqual 200
            }
        }

        on("pushing tasks") {
            git(githubToken)
                    .getRepository(courseName)
                    .branches()
                    .forEach { branch ->
                        teacherFiles[branch.name]
                                ?.forEach { filePath, file ->
                                    branch.commit(file, filePath)
                                    Thread.sleep(5 of TimeUnit.SECONDS)
                                }
                    }

            it("should add new files to tasks branches") {
                git(githubToken)
                        .getRepository(courseName)
                        .branches()
                        .forEach { branch ->
                            teacherFiles[branch.name]
                                    .orEmpty()
                                    .keys
                                    .also { fileNames ->
                                        branch.files()
                                                .map { it.name } shouldContainAll fileNames.toList()
                                    }
                        }
            }
        }

        on("starting course") {
            mockMvc.perform(post("/rest/course/activate")
                    .principal(principal())
                    .param("courseName", courseName)
            )

            val user = dataService.getUser(username)
                    ?: throw ModelException("User not found")

            val course = dataService.getCourse(courseName, user)
                    ?: throw ModelException("Course not found")

            it("should change course state lifecycle to running") {
                course.state.lifecycle shouldEqual CourseLifecycle.RUNNING
            }

            it("should add codacy and travis to activated services of a course") {
                course.state.activatedServices shouldContainAll listOf(
                        IntegratedService.TRAVIS,
                        IntegratedService.CODACY
                )
            }

            it("should activate codacy validations") {
                khttp
                        .get("$codacyApi/$githubId/$courseName",
                                headers = mapOf("api_token" to codacyToken)
                        )
                        .statusCode shouldEqual 200
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
                        .apply {
                            statusCode shouldEqual 200
                            jsonObject.getBoolean("active") shouldEqual true
                        }
            }
        }

        on("pushing solutions") {
            gitTokenToFiles.forEach { token, solutions ->
                git(token)
                        .forkRepository(githubId, courseName)
                        .also {
                            // It may be needed to have a delay between
                            // forking a repository and pushing files into it.
                            Thread.sleep(10 of TimeUnit.SECONDS)
                        }
                        .branches()
                        .mapNotNull { branch ->
                            solutions[branch.name]
                                    .orEmpty()
                                    .map { (filePath, file) ->
                                        branch.update(file, filePath)
                                                .also { Thread.sleep(5 of TimeUnit.SECONDS) }
                                    }
                                    .lastOrNull()
                        }
                        .also { solutionsCommits.addAll(it) }
            }

            it("should fork course repository for each student") {
                git(githubToken)
                        .getRepository(courseName)
                        .forks shouldEqual gitTokenToFiles.size
            }
        }

        on("creating solutions pull requests") {
            gitTokenToFiles
                    .mapValues { (_, solutions) -> solutions.keys }
                    .forEach { token, solutionsBranches ->
                        git(token).apply {
                            val targetRepository = getRepository(githubId, courseName)

                            getRepository(courseName)
                                    .branches()
                                    .filter { it.name in solutionsBranches }
                                    .forEach { sourceBranch ->
                                        targetRepository
                                                .branches()
                                                .find { it.name == sourceBranch.name }
                                                ?.also {
                                                    // Sleep is needed because codacy probably
                                                    // has some delay on getting new pull request
                                                    Thread.sleep(10 of TimeUnit.SECONDS)
                                                    sourceBranch.createPullRequestTo(it)
                                                }
                                                ?: throw GithubException("Branch not found")
                                    }
                        }
                    }

            val expectedBranchesBuilds =
                    gitTokenToFiles
                            .values
                            .flatMap { it.keys }
                            .groupingBy { it }
                            .eachCount()

            it("should initiate a travis validation for each solution") {
                repeatUntil("All travis builds starting",
                        retrievingDelay = 30,
                        attemptsLimit = 10) {
                    khttp
                            .get("$travisApi/repo/$githubId%2F$courseName/builds",
                                    headers = mapOf(
                                            "Authorization" to "token $travisToken",
                                            "Travis-API-Version" to "3"
                                    )
                            )
                            .run {
                                statusCode shouldEqual 200
                                val actualBranchesBuilds =
                                        jsonObject.getJSONArray("builds")
                                                .map { it as JSONObject }
                                                .map { it.getJSONObject("branch") }
                                                .map { it.getString("name") }
                                                .groupingBy { it }
                                                .eachCount()

                                expectedBranchesBuilds
                                        .all { (branchName, numberOfBuilds) ->
                                            actualBranchesBuilds[branchName]
                                                    ?.let { it >= numberOfBuilds }
                                                    ?: false
                                        }
                            }
                }
            }

            it("should initiate a codacy validation for each solution") {
                repeatUntil("All codacy analyses starting",
                        retrievingDelay = 30,
                        attemptsLimit = 10) {
                    solutionsCommits.all {
                        codacyService.codacy(githubId, codacyToken)
                                .commitDetails(courseName, it.sha)
                                .isRight
                    }
                }
            }
        }
    }
})