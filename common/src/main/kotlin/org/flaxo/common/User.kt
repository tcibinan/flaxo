package org.flaxo.common

class User(val githubId: String?,
           val nickname: String,
           val isGithubAuthorized: Boolean,
           val isTravisAuthorized: Boolean,
           val isCodacyAuthorized: Boolean
)
