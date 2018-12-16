package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Task
import org.flaxo.moss.MossSubmission
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.github.GithubManager
import java.nio.file.Files

class SimpleMossSubmissionsExtractor(private val githubManager: GithubManager,
                                     private val languages: List<Language>
) : MossSubmissionExtractor {

    companion object {
        private val logger = LogManager.getLogger(SimpleMossSubmissionsExtractor::class.java)
    }

    override fun extract(task: Task): MossSubmission {
        val user = task.course.user

        val course = task.course

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.nickname}.")

        val git = githubManager.with(githubToken)

        val language = languages.find { it.name == course.language }
                ?: throw UnsupportedLanguageException("Language ${course.language} is not supported for analysis yet.")

        logger.info("Aggregating ${task.friendlyId} task solutions to local file system")

        val submissionsDirectory = Files.createTempDirectory("moss-extracting")

        val completedTaskStudents = task.solutions.asSequence()
                .filter { it.buildReports.lastOrNull()?.succeed ?: false }
                .map { it.student }
                .map { it.nickname }
                .toList()

        val solutionFiles: List<LocalFile> =
                completedTaskStudents
                        .flatMap { student ->
                            logger.info("Aggregating student task solution ${task.friendlyId}/$student")
                            val branch = git.getRepository(student, course.name)
                                    .branches()
                                    .find { it.name == task.branch }
                            val studentFilesDirectory = submissionsDirectory.resolve(task.branch).resolve(student)
                            val files = branch?.files().orEmpty()
                                    .filter { it.suits(language) }
                                    .map { it.toLocalFile(studentFilesDirectory) }
                            logger.info("Student task solution ${task.friendlyId}/$student with ${files.size} files " +
                                    "was aggregated to $studentFilesDirectory")
                            files
                        }

        logger.info("Task ${task.friendlyId} solutions were aggregated successfully")

        logger.info("Aggregating ${task.friendlyId} task bases to local file system")

        val baseFiles: List<LocalFile> =
                git.getRepository(course.name)
                        .branches()
                        .find { it.name == task.branch }
                        ?.files()
                        .orEmpty()
                        .filter { it.suits(language) }
                        .map { it.toLocalFile(submissionsDirectory.resolve(task.branch).resolve("base")) }

        logger.info("Task ${course.friendlyId} bases were aggregated successfully")

        return MossSubmission(
                user = user.nickname,
                course = course.name,
                task = task.branch,
                language = language,
                students = completedTaskStudents,
                base = baseFiles,
                solutions = solutionFiles,
                tempDirectory = submissionsDirectory
        )
    }

    private fun EnvironmentFile.suits(language: Language) = language.extensions.any { fileName.endsWith(it) }

}
