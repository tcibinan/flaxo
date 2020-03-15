package org.flaxo.rest.manager.plag

import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.logging.log4j.LogManager
import org.flaxo.common.NotFoundException
import org.flaxo.common.data.PlagiarismReport as PlagiarismReportView
import org.flaxo.common.data.plagiarism.PlagiarismGraph
import org.flaxo.common.data.plagiarism.PlagiarismLink
import org.flaxo.common.data.plagiarism.PlagiarismNode
import org.flaxo.common.of
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismReport
import org.flaxo.rest.manager.CourseNotFoundException
import org.flaxo.rest.manager.PlagiarismReportNotFoundException
import org.flaxo.rest.manager.TaskNotFoundException
import org.flaxo.rest.manager.UserNotFoundException
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Basic plagiarism manager implementation.
 */
class BasicPlagiarismManager(private val dataManager: DataManager,
                             private val plagiarismAnalysisManager: PlagiarismAnalysisManager
) : PlagiarismManager {

    private val analyses: MutableMap<String, Long> = PassiveExpiringMap(1 of TimeUnit.HOURS)

    override fun analyse(userName: String, courseName: String, taskBranch: String): PlagiarismReportView {
        logger.info("Trying to start plagiarism analysis for $userName/$courseName")
        val user = dataManager.getUser(userName)
                ?: throw UserNotFoundException(userName)
        val course = dataManager.getCourse(courseName, user)
                ?: throw CourseNotFoundException(userName, courseName)
        val task = course.tasks.find { it.branch == taskBranch }
                ?: throw TaskNotFoundException(userName, courseName, taskBranch)

        return plagiarismAnalysisManager.analyse(task)
    }

    override fun generateGraphAccessToken(userName: String, courseName: String, taskBranch: String): String {
        logger.info("Trying to generate plagiarism report graph access token for task " +
                "$userName/$courseName/$taskBranch.")
        val user = dataManager.getUser(userName)
                ?: throw UserNotFoundException(userName)
        val course = dataManager.getCourse(courseName, user)
                ?: throw CourseNotFoundException(userName, courseName)
        val task = course.tasks.find { it.branch == taskBranch }
                ?: throw TaskNotFoundException(userName, courseName, taskBranch)
        val analysis = task.plagiarismReports.lastOrNull()
                ?: throw PlagiarismReportNotFoundException(userName, courseName, taskBranch)

        return UUID.randomUUID().toString().also {
            analyses[it] = analysis.id
        }
    }

    override fun getGraph(accessToken: String): PlagiarismGraph {
        logger.info("Trying to retrieve plagiarism graph by specified access token.")
        val analysisId = analyses[accessToken]
                ?: throw NotFoundException("No plagiarism graph was found for the specified access token.")
        val analysis = dataManager.getPlagiarismReport(analysisId)
                ?: throw NotFoundException("No plagiarism analysis with id $analysisId was found.")

        return PlagiarismGraph(nodes = extractNodes(analysis), links = extractLinks(analysis))
    }

    private fun extractNodes(analysis: PlagiarismReport): List<PlagiarismNode> =
            analysis.matches.asSequence()
                    .map { it.student1 to it.student2 }
                    .map { it.toList() }
                    .flatMap { it.asSequence() }
                    .distinct()
                    .map { PlagiarismNode(it) }
                    .toList()

    private fun extractLinks(analysis: PlagiarismReport) =
            analysis.matches.map { PlagiarismLink(it.student1, it.student2, it.percentage) }

    companion object {
        private val logger = LogManager.getLogger(BasicPlagiarismManager::class.java)
    }

}
