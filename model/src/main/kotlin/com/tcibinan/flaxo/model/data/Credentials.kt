package com.tcibinan.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Credentials data object.
 */
@Entity(name = "credentials")
@Table(name = "credentials")
data class Credentials(
        @Id
        @GeneratedValue
        override val id: Long? = null,

        val password: String = "",

        val githubToken: String? = null,

        val travisToken: String? = null,

        val codacyToken: String? = null
) : Identifiable {

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) = other is Credentials && other.id == id
}