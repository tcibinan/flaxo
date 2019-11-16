package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.common.Language
import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LocalFile
import org.flaxo.model.ModelException
import org.flaxo.model.data.Task
import org.flaxo.moss.MossSubmission
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.github.GithubManager
import java.nio.file.Files

/**
 * Basic Moss submissions extractor.
 *
 * It scans all repositories (both students and tutor) and retrieves files that suits analysis programming language
 * by extension. After all files are collected locally then a submission is returned.
 */
class SimpleMossSubmissionsExtractor(private val githubManager: GithubManager) : MossSubmissionExtractor {

    companion object {
        private val logger = LogManager.getLogger(SimpleMossSubmissionsExtractor::class.java)
    }

    override fun extract(task: Task): MossSubmission {
        val user = task.course.user

        val course = task.course

        course.settings.language
                ?: throw UnsupportedLanguageException("Course ${course.friendlyId} doesn\'t have language specified. " +
                        "Plagiarism analysis cannot be performed.")

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.name}.")

        val git = githubManager.with(githubToken)

        val language = course.settings.language
                ?.let { Language.from(it) }
                ?: throw UnsupportedLanguageException("Language ${course.settings.language} is not supported " +
                        "for plagiarism analysis yet.")

        logger.info("Aggregating ${task.friendlyId} task solutions to local file system")

        val submissionsDirectory = Files.createTempDirectory("moss-extracting")

        val completedTaskStudents = task.solutions.asSequence()
                .filter { it.buildReports.lastOrNull()?.succeed ?: false }
                .map { it.student }
                .map { it.name }
                .toList()

        val solutionFiles: List<LocalFile> =
                completedTaskStudents
                        .flatMap { student ->
                            logger.info("Aggregating student task solution ${task.friendlyId}/$student")
                            try {
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
                            } catch (e: Exception) {
                                logger.error("Student task solution ${task.friendlyId}/$student aggregation has failed. " +
                                        "The student solution will be skipped", e)
                                emptyList<LocalFile>()
                            }
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
                user = user.name,
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
