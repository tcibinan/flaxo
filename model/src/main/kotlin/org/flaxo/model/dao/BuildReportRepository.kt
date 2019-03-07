package org.flaxo.model.dao

import org.flaxo.model.data.BuildReport
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for build report entity.
 */
interface BuildReportRepository : CrudRepository<BuildReport, Long>
