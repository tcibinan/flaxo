package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * Travis api response build POJO.
 */
class RetrofitTravisBuildPOJO {

    var state: String = ""

    @JsonProperty("event_type")
    var eventType: String = ""

    @JsonProperty("pull_request_number")
    var pullRequestNumber: Int = 0

    @JsonProperty("finished_at")
    var finishedAt: LocalDateTime? = null

    lateinit var repository: RetrofitTravisRepositoryPOJO

    lateinit var branch: RetrofitTravisBranchPOJO

    lateinit var commit: RetrofitTravisCommitPOJO
}
