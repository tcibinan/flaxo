package org.flaxo.model

import org.flaxo.common.data.BuildReport
import org.flaxo.common.data.CodeStyleReport
import org.flaxo.common.data.Commit
import org.flaxo.common.data.Course
import org.flaxo.common.data.CourseState
import org.flaxo.common.data.CourseStatistics
import org.flaxo.common.data.CourseSettings
import org.flaxo.common.data.Language
import org.flaxo.common.data.PlagiarismMatch
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.Solution
import org.flaxo.common.data.Task
import org.flaxo.common.data.User

typealias LanguageView = Language
typealias CommitView = Commit
typealias BuildReportView = BuildReport
typealias CodeStyleReportView = CodeStyleReport
typealias SolutionView = Solution
typealias TaskView = Task
typealias CourseStatisticsView = CourseStatistics
typealias CourseSettingsView = CourseSettings
typealias CourseView = Course
typealias PlagiarismMatchView = PlagiarismMatch
typealias PlagiarismReportView = PlagiarismReport
typealias CourseStateView = CourseState
typealias UserView = User