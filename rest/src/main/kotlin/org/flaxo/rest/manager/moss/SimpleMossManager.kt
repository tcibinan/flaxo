package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataManager
import org.flaxo.model.data.PlagiarismMatch
import org.flaxo.model.data.Task
import org.flaxo.moss.MossSubmission
import org.flaxo.moss.MossSubmissionAnalyser
import org.flaxo.rest.friendlyId

/**
 * Moss manager implementation.
 */
class SimpleMossManager(private val dataManager: DataManager,
                        private val extractor: MossSubmissionExtractor,
                        private val analyser: MossSubmissionAnalyser
) : MossManager {

    override fun analyse(task: Task): Task {
        logger.info("Extracting moss submission for ${task.friendlyId} task")

        val submission: MossSubmission = extractor.extract(task)

        logger.info("Submitting moss submission for ${task.friendlyId} task")

        val mossResult = analyser.analyse(submission)

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
