package org.flaxo.rest.manager.moss

import org.apache.logging.log4j.LogManager
import org.flaxo.model.data.Task
import org.flaxo.moss.MossResult
import org.flaxo.moss.MossSubmissionAnalyser
import org.flaxo.rest.friendlyId
import org.flaxo.rest.manager.plag.PlagiarismAnalyser

/**
 * Moss plagiarism analyzer.
 */
class MossPlagiarismAnalyser(
        private val submissionExtractor: MossSubmissionExtractor,
        private val submissionAnalyser: MossSubmissionAnalyser
) : PlagiarismAnalyser {

    override fun analyse(task: Task): MossResult {
        logger.info("Extracting moss submission for ${task.friendlyId} task")

        val submission = submissionExtractor.extract(task)

        logger.info("Submitting moss submission for ${task.friendlyId} task")

        return submissionAnalyser.analyse(submission)
    }

    companion object {
        private val logger = LogManager.getLogger(MossPlagiarismAnalyser::class.java)
    }
}