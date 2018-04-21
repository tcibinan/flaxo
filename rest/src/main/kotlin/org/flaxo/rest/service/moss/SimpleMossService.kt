package org.flaxo.rest.service.moss

import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.language.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.MossResult
import org.flaxo.moss.SimpleMoss
import org.flaxo.moss.SimpleMossResult
import org.flaxo.rest.service.UnsupportedLanguage
import org.flaxo.rest.service.git.GitService
import it.zielke.moji.SocketClient
import org.jsoup.Jsoup
import java.net.URL
import java.nio.file.Paths

class SimpleMossService(private val userId: String,
                        private val gitService: GitService,
                        private val supportedLanguages: Map<String, Language>
) : MossService {

    override fun client(language: String): Moss =
            SimpleMoss(userId, language, SocketClient())

    override fun extractMossTasks(course: Course): List<MossTask> {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.nickname}.")

        val userGithubId = user.githubId
                ?: throw ModelException("Github id for user ${user.nickname} wasn't found.")

        val git = gitService.with(githubToken)

        val language = supportedLanguages[course.language]
                ?: throw UnsupportedLanguage("Language ${course.language} is not supported for analysis yet.")


        val tasksSolutions = course.students
                .map { student ->
                    student.nickname to
                            student.solutions
                                    .filter {
                                        val buildReport = it.buildReport
                                        buildReport != null && buildReport.succeed
                                    }
                                    .map { it.task.branch }
                }
                .map { (student, solvedTasks) ->
                    student to
                            git.branches(student, course.name)
                                    .filter { branch -> branch.name in solvedTasks }
                }
                .flatMap { (student, branches) ->
                    branches.map { student to it }
                }
                .groupBy { (_, branch) -> branch.name }
                .mapValues { (_, solutions) ->
                    solutions.flatMap { (student, branch) ->
                        branch.files()
                                .filterBy(language)
                                .map(toFileInFolder(student))
                    }
                }
                .toMap()

        val tasksBases =
                git.branches(userGithubId, course.name)
                        .map { branch ->
                            branch.name to branch.files()
                                    .filterBy(language)
                                    .map(toFileInFolder("base"))
                        }
                        .filter { (task, _) -> task in tasksSolutions.keys }
                        .filter { (_, files) -> files.isNotEmpty() }
                        .toMap()

        return tasksBases.keys
                .map { branch ->
                    Triple(
                            "${user.nickname}/${course.name}/$branch",
                            tasksBases[branch].orEmpty(),
                            tasksSolutions[branch].orEmpty()
                    )
                }
                .filter { (_, _, solutions) -> solutions.isNotEmpty() }
                .map { (taskName, base, solutions) ->
                    MossTask(taskName, base, solutions)
                }
    }

    override fun retrieveAnalysisResult(mossResultUrl: String): MossResult =
            SimpleMossResult(URL(mossResultUrl), { url -> Jsoup.connect(url) })

    private fun toFileInFolder(student: String): (EnvironmentFile) -> EnvironmentFile =
            { it.with("$student/${Paths.get(it.name).fileName}") }

}

private fun List<EnvironmentFile>.filterBy(language: Language): List<EnvironmentFile> =
        filter { file -> language.extensions.any { file.name.endsWith(it) } }

