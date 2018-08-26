package org.flaxo.common

/**
 * Flaxo user.
 */
class User(
        /**
         * User github nickname.
         */
        val githubId: String?,

        /**
         * User nickname.
         */
        val nickname: String,

        /**
         * Specifies if user is authorized in github using flaxo.
         */
        val isGithubAuthorized: Boolean,

        /**
         * Specifies if user is authorized in travis using flaxo.
         */
        val isTravisAuthorized: Boolean,

        /**
         * Specifies if user is authorized in codacy using flaxo.
         */
        val isCodacyAuthorized: Boolean
)
