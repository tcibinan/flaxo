package org.flaxo.model.dao

import org.flaxo.model.data.CourseSettings
import org.springframework.data.repository.CrudRepository

interface SettingsRepository : CrudRepository<CourseSettings, Long>
