package com.tcibinan.flaxo.rest.service.travis

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(
        "@type", "@href", "@representation", "@permissions",
        "avatar_url", "is_syncing", "synced_at"
)
class TravisUser() {
    var id: String = ""
    var login: String = ""
    var name: String = ""
    var github_id: Int = 0
}
