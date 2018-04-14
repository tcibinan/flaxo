package org.flaxo.moss

import java.net.URL

/**
 * Moss plagiarism detection analysis result.
 */
interface MossResult {

    /**
     * Moss platform result url.
     */
    val url: URL

    /**
     * Process and get results from the moss result url.
     *
     * @return All moss plagiarism matches.
     */
    fun matches(): Set<MossMatch>
}
