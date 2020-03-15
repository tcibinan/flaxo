package org.flaxo.rest.manager.gitplag

import io.gitplag.gitplagapi.model.input.AnalysisRequest
import org.flaxo.common.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Task
import org.flaxo.moss.MossMatch
import org.flaxo.moss.MossResult
import org.flaxo.rest.manager.PlagiarismAnalysisException
import org.flaxo.rest.manager.plag.PlagiarismAnalyser
import java.net.URL

/**
 * Gitplag plagiarism analyzer.
 */
class GitplagPlagiarismAnalyser(
        private val gitplagClient: GitplagClient,
        private val gitplagUiUrl: String
) : PlagiarismAnalyser {

    override fun analyse(task: Task): MossResult {
        val course = task.course

        val analyse = gitplagClient.analyse(
                vcsService = "github",
                username = course.user.githubId
                        ?: throw ModelException("Github id for ${course.user.name} user was not found"),
                projectName = course.name,
                analysisRequest = AnalysisRequest(
                        branch = task.branch,
                        language = toGitplagLanguage(Language.from(course.settings.language)),
                        analyzer = toGitplagAnalyser(course.settings.plagiarismBackend)
                )
        )

        val body = analyse.execute().body() ?: throw PlagiarismAnalysisException(
                user = task.course.user.name,
                course = task.course.name,
                task = task.branch
        )

        val matches = body.analysisPairs.map {
            MossMatch(
                    students = it.student1 to it.student2,
                    link = "$gitplagUiUrl/analyzes/${body.id}/pairs/${it.id}",
                    percentage = it.percentage,
                    lines = 0)
        }.toSet()

        return MossResult(
                url = URL("$gitplagUiUrl/analyzes/${body.id}"),
                matches = matches,
                students = course.students.map { it.name }
        )
    }
}
