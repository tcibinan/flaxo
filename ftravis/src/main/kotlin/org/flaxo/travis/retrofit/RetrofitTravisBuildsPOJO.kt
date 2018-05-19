package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Travis api response paginated list of builds POJO.
 */
class RetrofitTravisBuildsPOJO {

    var builds: List<RetrofitTravisBuildPOJO> = emptyList()

    @JsonProperty("@pagination")
    lateinit var pagination: TravisPaginationPOJO
}
