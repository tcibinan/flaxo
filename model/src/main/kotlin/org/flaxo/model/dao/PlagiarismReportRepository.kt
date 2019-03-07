package org.flaxo.model.dao

import org.flaxo.model.data.PlagiarismReport
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for build report entity.
 */
interface PlagiarismReportRepository : CrudRepository<PlagiarismReport, Long>
