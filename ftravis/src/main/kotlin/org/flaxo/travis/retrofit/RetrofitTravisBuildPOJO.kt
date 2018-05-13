package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class RetrofitTravisBuildPOJO {

    var state: String = ""

    var event_type: String = ""

    var pull_request_number: String = "0"

    lateinit var repository: RetrofitTravisRepositoryPOJO

    lateinit var branch: RetrofitTravisBranchPOJO

}
