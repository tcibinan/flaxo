package org.flaxo.codacy.response

import org.flaxo.codacy.model.CommitDelta
import org.flaxo.codacy.model.CommitUrl

/**
 * Commit delta response data object.
 */
data class CommitDeltaResponse(override val sha: String = "",
                               override val state: String = "",
                               override val urls: List<CommitUrl> = emptyList(),
                               val delta: CommitDelta = CommitDelta()
) : CommitResponse