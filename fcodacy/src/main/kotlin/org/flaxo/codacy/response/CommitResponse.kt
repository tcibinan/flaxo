package org.flaxo.codacy.response

import org.flaxo.codacy.model.CommitUrl

/**
 * Commit response basic interface.
 */
interface CommitResponse {
    val sha: String
    val state: String
    val urls: List<CommitUrl>
}