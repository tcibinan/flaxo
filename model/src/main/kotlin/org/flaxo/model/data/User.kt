package org.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.CascadeType
import javax.persistence.FetchType

/**
 * User entity.
 */
@Entity(name = "user")
@Table(name = "user",
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["nickname"]),
            UniqueConstraint(columnNames = ["githubId"])
        ]
)
data class User(

        @Id
        @GeneratedValue
        override val id: Long = -1,

        val nickname: String = "",

        val githubId: String? = null,

        @OneToOne(cascade = [CascadeType.ALL], optional = false, fetch = FetchType.LAZY)
        @JsonIgnoreProperties
        val credentials: Credentials = Credentials()

) : Identifiable, Viewable {

    override fun view(): Any = let { user ->
        object {
            val githubId = user.githubId
            val nickname = user.nickname
            val isGithubAuthorized = user.credentials.githubToken != null
            val isTravisAuthorized = user.credentials.travisToken != null
            val isCodacyAuthorized = user.credentials.codacyToken != null
        }
    }

    override fun toString(): String = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean =
            this::class.isInstance(other)
                    && (other as Identifiable).id == id
}