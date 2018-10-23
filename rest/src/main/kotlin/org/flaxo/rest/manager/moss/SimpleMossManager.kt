package org.flaxo.rest.manager.moss

import arrow.core.Try
import arrow.core.getOrElse
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.env.file.EnvironmentFile
import org.flaxo.core.lang.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.SimpleMoss
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.git.Branch
import org.flaxo.git.Git
import org.flaxo.model.data.Student
import org.flaxo.moss.MossSubmission
import org.apache.logging.log4j.LogManager
import org.flaxo.core.stringStackTrace
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.moss.MossException
import org.flaxo.rest.friendlyId
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Moss manager implementation.
 */
class SimpleMossManager(private val userId: String,
                        private val dataManager: DataManager,
                        private val githubManager: GithubManager,
                        private val languages: List<Language>,
                        executor: Executor? = null
) : MossManager {

    companion object {
        private val logger = LogManager.getLogger(SimpleMossManager::class.java)
    }

    private val executor: Executor = executor ?: Executors.newCachedThreadPool()

    override fun analysePlagiarism(course: Course): Course {
        logger.info("Extracting moss submissions for ${course.friendlyId}")

        val mossSubmissions: List<MossSubmission> = extractSubmissions(course)

        logger.info("${mossSubmissions.size} moss tasks were extracted for ${course.friendlyId}")

        logger.info("Scheduling moss submissions for ${course.friendlyId}")

        val analyses = submitToMoss(mossSubmissions, course)

        logger.info("Moss plagiarism analysis has been started for ${course.friendlyId}")

        Try {
            CompletableFuture.allOf(*analyses).get()
        }.getOrElse { e ->
            logger.error("Moss plagiarism analysis went bad for some of the submissions: ${e.stringStackTrace()}")
            throw MossException("Moss analysis went bad for some of the submissions", e)
        }

        return dataManager.getCourse(course.name, course.user)
                ?: throw MossException("Course ${course.friendlyId} was deleted during " +
                        "the plagiarism analysis")
    }

    private fun submitToMoss(mossSubmissions: List<MossSubmission>, course: Course)
            : Array<CompletableFuture<Void>> = synchronized(executor) {
        mossSubmissions.map { submission ->
            CompletableFuture.runAsync(Runnable { analyseMossSubmission(submission, course, course.tasks) }, executor)
        }
    }.toTypedArray()

    private fun analyseMossSubmission(submission: MossSubmission, course: Course, courseTasks: Set<Task>) {
        val branch = submission.branch

        val task = courseTasks.find { it.branch == branch }
                ?: throw MossException("Moss submission ${submission.friendlyId} " +
                        "target task ${course.name}/$branch was not found ")

        logger.info("Starting moss submission ${submission.friendlyId} for " +
                "${submission.base.size} bases files " +
                "and ${submission.solutions.size} solutions files")

        val mossResult = client(submission.language)
                .analyse(submission)

        logger.info("Moss submission ${submission.friendlyId} has finished successfully " +
                "and is available by ${mossResult.url}")

        val plagiarismReport = dataManager.addPlagiarismReport(
                task = task,
                url = mossResult.url.toString(),
                matches = mossResult.matches().map {
                    PlagiarismMatch(
                            student1 = it.students.first,
                            student2 = it.students.second,
                            lines = it.lines,
                            url = it.link,
                            percentage = it.percentage
                    )
                }
        )

        dataManager.updateTask(task.copy(
                plagiarismReports = task.plagiarismReports.plus(plagiarismReport)
        ))

        logger.info("Deleting moss submission ${submission.friendlyId} generated files.")

        (submission.base + submission.solutions)
                .forEach { Files.delete(it.localPath) }
    }

    private fun client(language: Language): Moss = SimpleMoss.of(userId, language)

    private fun extractSubmissions(course: Course): List<MossSubmission> {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw ModelException("Github credentials wasn't found for user ${user.nickname}.")

        val git = githubManager.with(githubToken)

        val language = languages.find { it.name == course.language }
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
                            user = user.nickname,
                            course = course.name,
                            branch = branch,
                            language = language,
                            base = taskToBaseFiles[branch].orEmpty(),
                            solutions = taskToSolutionFiles[branch].orEmpty()
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

    private fun List<EnvironmentFile>.filterBy(language: Language): List<EnvironmentFile> =
            filter { file -> language.extensions.any { file.fileName.endsWith(it) } }

}
