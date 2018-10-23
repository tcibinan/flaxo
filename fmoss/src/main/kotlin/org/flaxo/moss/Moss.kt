package org.flaxo.moss

/**
 * Moss plagiarism detection analysis client.
 */
interface Moss {

    /**
     * Start the moss plagiarism submission analysis.
     *
     * @return Result of the moss analysis.
     */
    fun analyse(submission: MossSubmission): MossResult
}
