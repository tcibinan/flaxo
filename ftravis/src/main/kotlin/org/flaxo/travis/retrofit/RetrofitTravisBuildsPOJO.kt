package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class RetrofitTravisBuildsPOJO {

    var builds: List<RetrofitTravisBuildPOJO> = emptyList()

}
