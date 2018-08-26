package org.flaxo.model.data

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

/**
 * Credentials entity.
 */
@Entity(name = "credentials")
@Table(name = "credentials")
data class Credentials(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val password: String = "",

        val githubToken: String? = null,

        val travisToken: String? = null,

        val codacyToken: String? = null

) : Identifiable {

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?) =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}