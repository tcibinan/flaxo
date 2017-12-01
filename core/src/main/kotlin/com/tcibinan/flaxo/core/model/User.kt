package com.tcibinan.flaxo.core.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.CascadeType

@Entity
data class User(
        @Id @GeneratedValue
        val user_id: Long,
        val nickname: String,
        @OneToOne(cascade = arrayOf(CascadeType.ALL))
        val credentials: Credentials
)