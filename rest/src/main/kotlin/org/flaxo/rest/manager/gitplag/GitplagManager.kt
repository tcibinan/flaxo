package org.flaxo.rest.manager.gitplag

import arrow.core.Either
import io.gitplag.gitplagapi.model.enums.AnalysisMode
import io.gitplag.gitplagapi.model.enums.AnalyzerProperty
import io.gitplag.gitplagapi.model.enums.GitProperty
import io.gitplag.gitplagapi.model.input.RepositoryInput
import okhttp3.ResponseBody
import org.flaxo.common.Language
import org.flaxo.model.data.Course
import org.flaxo.moss.GitplagClient
import org.flaxo.moss.toGitplagLanguage
import org.flaxo.rest.manager.ValidationManager
import retrofit2.Call

class GitplagManager(
        private val gitplagClient: GitplagClient
) : ValidationManager {
    override fun activate(course: Course) {
        gitplagClient.addRepository(RepositoryInput(
                id = -1,
                git = GitProperty.GITHUB,
                language = toGitplagLanguage(Language.from(course.settings.language)),
                name = course.user.githubId!! + "/" + course.name,
                analyzer = AnalyzerProperty.MOSS,
                filePatterns = listOf(""".+\.java"""),
                analysisMode = AnalysisMode.FULL
        )).callUnit()
        gitplagClient.updateRepository("github", course.user.githubId!!, course.name).callUnit()
    }

    override fun deactivate(course: Course) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun refresh(course: Course) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body()!!)
                else Either.left(errorBody()!!)
            }

    private fun <T> Call<T>.callUnit(): ResponseBody? =
            execute().run { if (isSuccessful) null else errorBody() }
}