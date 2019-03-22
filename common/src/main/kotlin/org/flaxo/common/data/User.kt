package org.flaxo.common.data

import org.flaxo.common.DateTime

/**
 * Flaxo user.
 */
data class User(

        override val id: Long,

        override val name: String,

        override val date: DateTime,

        /**
         * User github nickname.
         */
        val githubId: String?,

        /**
         * User nickname.
         */
        @Deprecated("Should be replaced with just a name.", replaceWith = ReplaceWith("name"))
        val nickname: String = name,

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
) : Identifiable, Named, Dated
