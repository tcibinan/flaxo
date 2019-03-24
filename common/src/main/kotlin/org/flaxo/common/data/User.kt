package org.flaxo.common.data

import kotlinx.serialization.Serializable
import org.flaxo.common.DateTime

/**
 * Flaxo user.
 */
@Serializable
data class User(

        override val id: Long,

        override val name: String,

        override val date: DateTime,

        /**
         * User github nickname.
         */
        val githubId: String?,

        /**
         * Specifies if user is authorized in github using flaxo.
         */
        val githubAuthorized: Boolean,

        /**
         * Specifies if user is authorized in travis using flaxo.
         */
        val travisAuthorized: Boolean,

        /**
         * Specifies if user is authorized in codacy using flaxo.
         */
        val codacyAuthorized: Boolean
) : Identifiable, Named, Dated
