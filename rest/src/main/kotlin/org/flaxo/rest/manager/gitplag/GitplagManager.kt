package org.flaxo.rest.manager.gitplag

import arrow.core.Either
import io.gitplag.gitplagapi.model.enums.AnalysisMode
import io.gitplag.gitplagapi.model.enums.AnalyzerProperty
import io.gitplag.gitplagapi.model.enums.GitProperty
import io.gitplag.gitplagapi.model.input.RepositoryInput
import io.gitplag.gitplagapi.model.input.RepositoryUpdate
import okhttp3.ResponseBody
import org.flaxo.common.Language
import org.flaxo.model.ModelException
import org.flaxo.model.data.Course
import org.flaxo.rest.manager.UnsupportedLanguageException
import org.flaxo.rest.manager.ValidationManager
import retrofit2.Call

/**
 * Gitplag manager.
 */
class GitplagManager(private val gitplagClient: GitplagClient) : ValidationManager {

    private val github = "github"
    private val defaultAnalyzer = AnalyzerProperty.MOSS
    private val defaultAnalysisMode = AnalysisMode.FULL

    override fun activate(course: Course) {
        val userGithubId = course.user.githubId
                ?: throw ModelException("Github id for ${course.user.name} user was not found")
        addRepository(userGithubId, course)
        updateRepositoryFiles(userGithubId, course.name)
    }

    override fun deactivate(course: Course) = Unit

    override fun refresh(course: Course) {
        val userGithubId = course.user.githubId
                ?: throw ModelException("Github id for ${course.user.name} user was not found")
        updateRepository(userGithubId, course)
    }

    /**
     * Deletes course files and then downloads them again
     */
    fun reloadFiles(course: Course) {
        val userGithubId = course.user.githubId
                ?: throw ModelException("Github id for ${course.user.name} user was not found")
        deleteBaseFiles(userGithubId, course.name)
        deleteSolutionFiles(userGithubId, course.name)
        updateRepositoryFiles(userGithubId, course.name)
    }

    private fun addRepository(userGithubId: String, course: Course) {
        val language = Language.from(course.settings.language)
                ?: throw UnsupportedLanguageException("Course ${course.name} does not have language property")
        gitplagClient.addRepository(RepositoryInput(
                git = GitProperty.GITHUB,
                language = toGitplagLanguage(language),
                name = "$userGithubId/${course.name}",
                analyzer = defaultAnalyzer,
                filePatterns = listOf(course.settings.plagiarismFilePatterns ?: language.buildExtensionRegexp()),
                analysisMode = defaultAnalysisMode
        )).callUnit()
    }

    private fun updateRepository(userGithubId: String, course: Course) {
        val language = Language.from(course.settings.language)
                ?: throw UnsupportedLanguageException("Course ${course.name} does not have language property")
        gitplagClient.updateRepository(
                github,
                userGithubId,
                course.name,
                RepositoryUpdate(
                        language = toGitplagLanguage(language),
                        analyzer = defaultAnalyzer,
                        filePatterns = listOf(course.settings.plagiarismFilePatterns
                                ?: language.buildExtensionRegexp()),
                        analysisMode = defaultAnalysisMode
                )).callUnit()
    }

    private fun deleteBaseFiles(username: String, projectName: String) = gitplagClient.deleteBaseFiles(
            vcsService = github,
            username = username,
            projectName = projectName
    ).callUnit()

    private fun deleteSolutionFiles(username: String, projectName: String) = gitplagClient.deleteSolutionFiles(
            vcsService = github,
            username = username,
            projectName = projectName
    ).callUnit()

    private fun updateRepositoryFiles(username: String, projectName: String) = gitplagClient.updateRepositoryFiles(
            vcsService = github,
            username = username,
            projectName = projectName
    ).callUnit()

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body()!!)
                else Either.left(errorBody()!!)
            }

    private fun <T> Call<T>.callUnit(): ResponseBody? =
            execute().run { if (isSuccessful) null else errorBody() }

    private fun Language.buildExtensionRegexp() =
            """.+\.""" + extensions.joinToString(separator = "|", prefix = "(", postfix = ")")
}
