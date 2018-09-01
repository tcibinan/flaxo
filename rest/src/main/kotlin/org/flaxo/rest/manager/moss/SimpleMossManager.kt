package org.flaxo.rest.manager.moss

import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.lang.Language
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
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.model.data.Student
import org.jsoup.Jsoup
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

/**
 * Moss manager.
 */
class SimpleMossManager(private val userId: String,
                        private val githubManager: GithubManager,
                        private val supportedLanguages: Map<String, Language>
) : MossManager {

    private val logger = LogManager.getLogger(SimpleMossManager::class.java)

    override fun client(language: String): Moss = SimpleMoss(userId, language, SocketClient())

    override fun extractSubmissions(course: Course): List<MossSubmission> {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.nickname}.")

        val git = githubManager.with(githubToken)

        val language = supportedLanguages[course.language]
                ?: throw UnsupportedLanguageException("Language ${course.language} is not supported for analysis yet.")

        logger.info("Aggregating course ${user.nickname}/${course.name} solutions as the local files")

        val submissionsDirectory = Files.createTempDirectory("moss-extracting")

        val taskToSolutionFiles: Map<String, List<LocalFile>> =
                course.students
                        .map { student -> student.nickname to onlySolvedTasks(student) }
                        .map { (student, solvedTasks) -> student to taskBranches(git, student, course, solvedTasks) }
                        .flatMap { (student, branches) -> branches.map { student to it } }
                        .groupBy { (_, branch) -> branch.name }
                        .mapValues { (_, solutions) ->
                            solutions.flatMap { (student, branch) ->
                                branchFiles(branch, language, submissionsDirectory, student)
                                        .also {
                                            logger.info("Student solution $student/${branch.name} was aggregated " +
                                                    "as local files for course ${user.nickname}/${course.name}")
                                        }
                            }
                        }
                        .toMap()

        logger.info("Course ${user.nickname}/${course.name} solutions was aggregated successfully: " +
                "${filesSummary(taskToSolutionFiles)}")

        logger.info("Aggregating course ${user.nickname}/${course.name} bases as the local files")

        val taskToBaseFiles: Map<String, List<LocalFile>> =
                git.getRepository(course.name)
                        .branches()
                        .map { branch -> branch.name to branchFiles(branch, language, submissionsDirectory, "base") }
                        .filter { (task, _) -> task in taskToSolutionFiles.keys }
                        .filter { (_, files) -> files.isNotEmpty() }
                        .toMap()

        logger.info("Course ${user.nickname}/${course.name} solutions was aggregated successfully: " +
                "${filesSummary(taskToBaseFiles)}")

        val tasks: Set<String> = taskToBaseFiles.keys

        return tasks
                .filter { branch -> taskToSolutionFiles[branch] != null }
                .map { branch ->
                    MossSubmission(
                            base = taskToBaseFiles[branch].orEmpty(),
                            solutions = taskToSolutionFiles[branch].orEmpty(),
                            user = user.nickname,
                            course = course.name,
                            branch = branch
                    )
                }
    }

    private fun branchFiles(branch: Branch,
                            language: Language,
                            localFilesDirectory: Path,
                            student: String
    ): List<LocalFile> =
            branch.files()
                    .filterBy(language)
                    .map { it.toLocalFile(localFilesDirectory.resolve(branch.name).resolve(student)) }

    private fun onlySolvedTasks(student: Student): List<String> =
            student.solutions
                    .filter { it.buildReports.isNotEmpty() && it.buildReports.last().succeed }
                    .map { it.task.branch }

    private fun taskBranches(git: Git,
                             student: String,
                             course: Course,
                             solvedTasks: List<String>
    ): List<Branch> =
            git.getRepository(student, course.name)
                    .branches()
                    .filter { branch -> branch.name in solvedTasks }

    private fun filesSummary(tasksBases: Map<String, List<EnvironmentFile>>) =
            tasksBases.map { (task, bases) -> task to bases.map { it.path } }

    override fun retrieveAnalysisResult(mossResultUrl: String): MossResult =
            SimpleMossResult(URL(mossResultUrl)) { url -> Jsoup.connect(url) }

    private fun List<EnvironmentFile>.filterBy(language: Language): List<EnvironmentFile> =
            filter { file -> language.extensions.any { file.fileName.endsWith(it) } }

}

