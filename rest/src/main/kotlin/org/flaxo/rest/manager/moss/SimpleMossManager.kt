package org.flaxo.rest.manager.moss

import org.flaxo.core.env.EnvironmentFile
import org.flaxo.core.language.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.MossResult
import org.flaxo.moss.SimpleMoss
import org.flaxo.moss.SimpleMossResult
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.github.GithubManager
import it.zielke.moji.SocketClient
import org.apache.logging.log4j.LogManager
import org.jsoup.Jsoup
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/**
 * Moss service basic implementation.
 */
class SimpleMossManager(private val userId: String,
                        private val githubManager: GithubManager,
                        private val supportedLanguages: Map<String, Language>
) : MossManager {

    private val logger = LogManager.getLogger(SimpleMossManager::class.java)

    override fun client(language: String): Moss =
            SimpleMoss(userId, language, SocketClient())

    override fun extractMossTasks(course: Course): List<MossTask> {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.nickname}.")

        val git = githubManager.with(githubToken)

        val language = supportedLanguages[course.language]
                ?: throw UnsupportedLanguageException("Language ${course.language} is not supported for analysis yet.")

        logger.info("Aggregating course ${user.nickname}/${course.name} solutions as the local files")

        // TODO: 02/06/18 Figure out how to delete temp folder
        val tempFilesPath = Files.createTempDirectory("moss-extracting")

        val tasksSolutions = course.students
                .map { student ->
                    student.nickname to
                            student.solutions
                                    .filter {
                                        it.buildReports.isNotEmpty()
                                                && it.buildReports.last().succeed
                                    }
                                    .map { it.task.branch }
                }
                .map { (student, solvedTasks) ->
                    student to
                            git.getRepository(student, course.name)
                                    .branches()
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
                                .map(toFileInFolder(tempFilesPath, student)).also {
                                    logger.info("Student $student solutions were aggregated as local files " +
                                            "for course ${user.nickname}/${course.name}")
                                }
                    }
                }
                .toMap()

        logger.info("Course ${user.nickname}/${course.name} solutions was aggregated successfully: " +
                "${filesSummary(tasksSolutions)}")

        logger.info("Aggregating course ${user.nickname}/${course.name} bases as the local files")

        val tasksBases =
                git.getRepository(course.name)
                        .branches()
                        .map { branch ->
                            branch.name to branch.files()
                                    .filterBy(language)
                                    .map(toFileInFolder(tempFilesPath, "base"))
                        }
                        .filter { (task, _) -> task in tasksSolutions.keys }
                        .filter { (_, files) -> files.isNotEmpty() }
                        .toMap()

        logger.info("Course ${user.nickname}/${course.name} solutions was aggregated successfully: " +
                "${filesSummary(tasksBases)}")

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

    private fun filesSummary(tasksBases: Map<String, List<EnvironmentFile>>) =
            tasksBases.map { (task, bases) -> task to bases.map { it.path } }

    override fun retrieveAnalysisResult(mossResultUrl: String): MossResult =
            SimpleMossResult(URL(mossResultUrl), { url -> Jsoup.connect(url) })

    private fun toFileInFolder(folder: Path,
                               student: String
    ): (EnvironmentFile) -> EnvironmentFile =
            { it.inFolder(folder.resolve(student)) }

    private fun List<EnvironmentFile>.filterBy(language: Language)
            : List<EnvironmentFile> =
            filter { file -> language.extensions.any { file.fileName.endsWith(it) } }

}

