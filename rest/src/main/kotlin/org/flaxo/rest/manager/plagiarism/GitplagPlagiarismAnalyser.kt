package org.flaxo.rest.manager.plagiarism

import io.gitplag.gitplagapi.model.input.AnalysisRequest
import org.flaxo.common.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Task
import org.flaxo.moss.MossMatch
import org.flaxo.moss.MossResult
import org.flaxo.rest.manager.gitplag.GitplagClient
import org.flaxo.rest.manager.gitplag.toGitplagLanguage
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
                "github",
                course.user.githubId
                        ?: throw ModelException("Github id for ${course.user.name} user was not found"),
                course.name,
                AnalysisRequest(
                        branch = task.branch,
                        language = toGitplagLanguage(Language.from(task.course.settings.language))
                )
        )

        val body = analyse.execute().body()!!

        val matches = body.analysisPairs.map {
            MossMatch(
                    students = it.student1 to it.student2,
                    link = gitplagUiUrl + "/analyzes/" + body.id + "/pairs/" + it.id,
                    percentage = it.percentage,
                    lines = 0)
        }.toSet()

        return MossResult(
                url = URL(gitplagUiUrl + "/analyzes/" + body.id),
                matches = matches,
                students = course.students.map { it.name }
        )
    }
}
