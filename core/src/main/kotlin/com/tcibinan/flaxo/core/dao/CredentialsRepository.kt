package com.tcibinan.flaxo.core.dao

import com.tcibinan.flaxo.core.model.Credentials
import org.springframework.data.repository.CrudRepository

interface CredentialsRepository : CrudRepository<Credentials, Long> {
}