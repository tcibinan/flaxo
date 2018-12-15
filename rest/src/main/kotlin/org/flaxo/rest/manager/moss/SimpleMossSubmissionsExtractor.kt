package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.Language
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.model.data.Student
import org.flaxo.model.data.Task
import org.flaxo.moss.MossSubmission
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.github.GithubManager
import java.nio.file.Files
import java.nio.file.Path

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

        logger.info("Aggregating task ${task.friendlyId} solutions to local file system")

        val submissionsDirectory = Files.createTempDirectory("moss-extracting")

        val solutionFiles: List<LocalFile> =
                course.students.flatMap { student ->
                    studentLocalFiles(git, course, task, language, student, submissionsDirectory)
                }

        logger.info("Course ${course.friendlyId} solutions was aggregated successfully: " +
                "${solutionFiles.map { it.path }}")

        logger.info("Aggregating course ${user.nickname}/${course.name} bases as the local files")

        val baseFiles: List<LocalFile> =
                git.getRepository(course.name)
                        .branches()
                        .find { it.name == task.branch }
                        ?.branchFiles(language, submissionsDirectory, "base")
                        ?: emptyList()

        logger.info("Course ${course.friendlyId} solutions was aggregated successfully: " +
                "${baseFiles.map { it.path }}")

        return MossSubmission(
                user = user.nickname,
                course = course.name,
                branch = task.branch,
                language = language,
                base = baseFiles,
                solutions = solutionFiles
        )
    }

    private fun studentLocalFiles(git: Git,
                                  course: Course,
                                  task: Task,
                                  language: Language,
                                  student: Student,
                                  localFilesDirectory: Path
    ): List<LocalFile> =
            git.getRepository(student.nickname, course.name)
                    .branches()
                    .filter { it.name in task.branch }
                    .flatMap { branch ->
                        branch.branchFiles(language, localFilesDirectory, student.nickname)
                                .also {
                                    logger.info("Student solution ${task.friendlyId}/${student.nickname} " +
                                            "was aggregated")
                                }
                    }

    private fun Branch.branchFiles(language: Language, localFilesDirectory: Path, student: String): List<LocalFile> =
            files().asSequence()
                    .filter { it.suits(language) }
                    .map { it.toLocalFile(localFilesDirectory.resolve(name).resolve(student)) }
                    .toList()

    private fun EnvironmentFile.suits(language: Language) = language.extensions.any { fileName.endsWith(it) }

}
