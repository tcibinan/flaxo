package org.flaxo.rest.manager.plag

import org.apache.logging.log4j.LogManager
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.rest.friendlyId

/**
 * Moss manager implementation.
 */
class SimplePlagiarismAnalysisManager(private val dataManager: DataManager,
                                      private val plagiarismAnalyser: PlagiarismAnalyser
) : PlagiarismAnalysisManager {

    override fun analyse(task: Task): PlagiarismReport {
        val mossResult = plagiarismAnalyser.analyse(task)

        logger.info("Updating ${task.friendlyId} task plagiarism reports")

        val plagiarismReport = dataManager.addPlagiarismReport(
                task = task,
                url = mossResult.url.toString(),
                matches = mossResult.matches.map {
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

        return plagiarismReport.view()
    }

    companion object {
        private val logger = LogManager.getLogger(SimplePlagiarismAnalysisManager::class.java)
    }
}
