package org.flaxo.model.dao

import org.flaxo.model.data.CodeStyleReport
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for build report entity.
 */
interface CodeStyleReportRepository : CrudRepository<CodeStyleReport, Long>