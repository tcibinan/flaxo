package org.flaxo.moss

/**
 * Moss submission analyser.
 */
interface MossSubmissionAnalyser {

    /**
     * Sends the given [submission] to the moss server, waits for the analysis results
     * and parses the analysis outputs.
     */
    fun analyse(submission: MossSubmission): MossResult

}
