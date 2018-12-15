package org.flaxo.moss

import java.net.URL

/**
 * Moss plagiarism detection analysis client.
 */
interface Moss {

    /**
     * Start the moss plagiarism submission analysis.
     *
     * @return Moss analysis result [URL].
     */
    fun submit(submission: MossSubmission): URL
}
