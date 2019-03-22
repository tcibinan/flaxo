package org.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.flaxo.common.DateTime
import org.flaxo.model.UserView
import java.time.LocalDateTime
import java.util.Objects
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint

/**
 * User entity.
 */
@Entity(name = "user")
@Table(name = "flaxo_user",
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

) : Identifiable, Viewable<UserView> {

    override fun view(): UserView = UserView(
            id = id,
            name = nickname,
            // TODO 23.03.19: Add date field to user entity.
            date = DateTime(LocalDateTime.now()),
            githubId = githubId,
            isGithubAuthorized = credentials.githubToken != null,
            isTravisAuthorized = credentials.travisToken != null,
            isCodacyAuthorized = credentials.codacyToken != null
    )

    override fun toString() = "${this::class.simpleName}(id=$id)"

    override fun hashCode() = Objects.hash(id)

    override fun equals(other: Any?): Boolean = this::class.isInstance(other) && other is Identifiable && other.id == id

}