package com.tcibinan.flaxo.model.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.UniqueConstraint
import javax.persistence.CascadeType

/**
 * User data object.
 */
@Entity(name = "user")
@Table(name = "user", uniqueConstraints = [UniqueConstraint(columnNames = ["nickname"])])
@JsonIgnoreProperties("credentials")
data class User(
        @Id
        @GeneratedValue
        val userId: Long? = null,

        val nickname: String = "",

        val githubId: String? = null,

        @OneToOne(cascade = [CascadeType.ALL])
        val credentials: Credentials = Credentials()
) : Viewable {

    override fun view(): Any = let { user ->
        object {
            val id = user.userId
            val githubId = user.githubId
            val nickname = user.nickname
            val isGithubAuthorized = user.credentials.githubToken != null
            val isTravisAuthorized = user.credentials.travisToken != null
            val isCodacyAuthorized = user.credentials.codacyToken != null
        }
    }

    override fun hashCode() = Objects.hash(userId)

    override fun equals(other: Any?) = other is User && other.userId == userId

}