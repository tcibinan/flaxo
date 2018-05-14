package org.flaxo.travis.webhook

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Travis web hook repository class.
 *
 * Uses to map json to object model.
 */
class TravisWebHookRepository(

    val name: String = "",

    @JsonProperty("owner_name")
    val ownerName: String = ""
)