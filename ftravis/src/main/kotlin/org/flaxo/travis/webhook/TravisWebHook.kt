package org.flaxo.travis.webhook

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Travis web hook class.
 *
 * Uses to map json to object model.
 */
class TravisWebHook(
    @JsonProperty("status_message")
    val statusMessage: String = "",

    val type: String = "",

    val branch: String = "",

    @JsonProperty("pull_request_number")
    val pullRequestNumber: String? = null,

    val commit: String = "",

    @JsonProperty("finished_at")
    val finishedAt: LocalDateTime? = null,

    val repository: TravisWebHookRepository = TravisWebHookRepository()
)

