package org.flaxo.codacy.response

import org.flaxo.codacy.model.CommitDetails
import org.flaxo.codacy.model.CommitUrl

/**
 * Commit details response data object.
 */
data class CommitDetailsResponse(override val sha: String = "",
                                 override val state: String = "",
                                 override val urls: List<CommitUrl> = emptyList(),
                                 val commit: CommitDetails = CommitDetails()
) : CommitResponse