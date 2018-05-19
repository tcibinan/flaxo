package org.flaxo.travis.retrofit

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Travis api response pagination POJO.
 */
class TravisPaginationPOJO {

    @JsonProperty("is_last")
    var last: Boolean = false

    var offset: Int = 0
}
