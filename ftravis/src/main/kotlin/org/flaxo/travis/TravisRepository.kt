package org.flaxo.travis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Travis repository class.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TravisRepository {
    var id: Int = 0
    var name: String = ""
    var slug: String = ""
    var active: Boolean = true
    var private: Boolean = false
    lateinit var owner: TravisUser
}