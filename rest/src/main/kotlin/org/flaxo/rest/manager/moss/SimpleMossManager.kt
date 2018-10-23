package org.flaxo.rest.manager.moss

import arrow.core.Try
import arrow.core.getOrElse
import org.flaxo.core.lang.Language
import org.flaxo.model.data.Course
import org.flaxo.moss.Moss
import org.flaxo.moss.SimpleMoss
import org.flaxo.moss.MossSubmission
import org.apache.logging.log4j.LogManager
import org.flaxo.core.stringStackTrace
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.moss.MossException
import org.flaxo.rest.friendlyId
import java.nio.file.Files
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Moss manager implementation.
 */
class SimpleMossManager(private val userId: String,
                        private val dataManager: DataManager,
                        private val extractor: MossSubmissionsExtractor,
                        executor: Executor? = null
) : MossManager {

    companion object {
        private val logger = LogManager.getLogger(SimpleMossManager::class.java)
    }

    private val executor: Executor = executor ?: Executors.newCachedThreadPool()

    override fun analysePlagiarism(course: Course): Course {
        logger.info("Extracting moss submissions for ${course.friendlyId}")

        val mossSubmissions: List<MossSubmission> = extractor.extract(course)

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

}
