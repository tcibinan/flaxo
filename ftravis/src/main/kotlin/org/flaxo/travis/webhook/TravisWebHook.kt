package org.flaxo.travis.webhook

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Travis web hook class.
 *
 * Uses to map json to object model.
 */
class TravisWebHook {

    @JsonProperty("status_message")
    var statusMessage: String = ""

    var type: String = ""

    var branch: String = ""

    @JsonProperty("pull_request_number")
    var pullRequestNumber: String? = null

    var commit: String = ""

    @JsonProperty("finished_at")
    var finishedAt: LocalDateTime? = null

    lateinit var repository: TravisWebHookRepository
}

