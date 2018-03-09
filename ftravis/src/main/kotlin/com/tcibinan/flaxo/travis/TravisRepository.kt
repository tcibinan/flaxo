package com.tcibinan.flaxo.travis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Travis repository class.
 */
@JsonIgnoreProperties(
        "@type", "@href", "@representation", "@permissions",
        "default_branch", "starred", "description", "github_language"
)
class TravisRepository {
    var id: Int = 0
    var name: String = ""
    var slug: String = ""
    var active: Boolean = true
    var private: Boolean = false
    lateinit var owner: TravisUser
}