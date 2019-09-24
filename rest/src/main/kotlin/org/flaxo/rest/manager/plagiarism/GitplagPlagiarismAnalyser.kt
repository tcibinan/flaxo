package org.flaxo.rest.manager.plagiarism

import org.flaxo.model.data.Task
import org.flaxo.moss.GitplagClient
import org.flaxo.moss.MossMatch
import org.flaxo.moss.MossResult
import org.flaxo.moss.model.AnalysisRequest
import java.net.URL

class GitplagPlagiarismAnalyser(
        private val gitplagClient: GitplagClient
) : PlagiarismAnalyser {
    override fun analyse(task: Task): MossResult {
        val course = task.course

        val analyse = gitplagClient.analyse(
                "github",
                course.user.githubId!!,
                course.name,
                AnalysisRequest(branch = task.branch, language = task.course.settings.language ?: "text")
        )

        val body = analyse.execute().body()!!

        val matches = body.analysisPairs.map {
            MossMatch(
                    students = it.student1 to it.student2,
                    link = "",
                    percentage = it.percentage,
                    lines = 0)
        }.toSet()

        return MossResult(
                url = URL("http://localhost:8090" + body.resultLink),
                matches = matches,
                students = course.students.map { it.nickname }
        )
    }
}
