package org.flaxo.rest.manager.plag

import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.plagiarism.PlagiarismGraph

/**
 * Plagiarism analysis manager.
 */
interface PlagiarismManager {

    /**
     * Launches a plagiarism analysis.
     *
     * It is non-blocking and returns immediately.
     */
    fun analyse(userName: String, courseName: String, taskBranch: String): PlagiarismReport

    /**
     * Generates a plagiarism graph temporary access token.
     */
    fun generateGraphAccessToken(userName: String, courseName: String, taskBranch: String): String

    /**
     * Retrieves a plagiarism graph by the given temporary access token.
     */
    fun getGraph(accessToken: String): PlagiarismGraph
}
