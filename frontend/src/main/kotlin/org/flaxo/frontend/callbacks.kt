package org.flaxo.frontend

import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.Task

typealias OnCourseChange = (Course) -> Unit
typealias OnCourseStatisticsChange = (CourseStatistics) -> Unit
typealias OnTaskChange = (Task) -> Unit
