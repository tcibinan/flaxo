package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.plagiarism.PlagiarismAnalyser

/**
 * Moss manager implementation.
 */
class SimpleMossManager(private val dataManager: DataManager,
                        private val plagiarismAnalyser: PlagiarismAnalyser
) : MossManager {

    override fun analyse(task: Task): Task {

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

        return dataManager.updateTask(task.copy(
                plagiarismReports = task.plagiarismReports.plus(plagiarismReport)
        ))
    }

    companion object {
        private val logger = LogManager.getLogger(SimpleMossManager::class.java)
    }
}
