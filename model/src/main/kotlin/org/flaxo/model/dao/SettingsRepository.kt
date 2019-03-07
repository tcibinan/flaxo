package org.flaxo.model.dao

import org.flaxo.model.data.CourseSettings
import org.springframework.data.repository.CrudRepository

/**
 * Crud repository for course settings entity.
 */
interface SettingsRepository : CrudRepository<CourseSettings, Long>
