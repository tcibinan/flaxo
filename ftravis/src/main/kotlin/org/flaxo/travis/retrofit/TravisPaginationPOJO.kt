package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class TravisPaginationPOJO {

    @JsonProperty("is_last")
    var isLast: Boolean = false

    var offset: Int = 0
}
