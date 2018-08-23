package org.flaxo.frontend.data

class User(val githubId: String?,
           val nickname: String,
           val isGithubAuthorized: Boolean,
           val isTravisAuthorized: Boolean,
           val isCodacyAuthorized: Boolean)