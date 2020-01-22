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
    private val defaultGit = GitProperty.GITHUB
    private val defaultAnalyzer = AnalyzerProperty.MOSS
    private val defaultAnalysisMode = AnalysisMode.FULL

    override fun activate(course: Course) {
        addRepository(course)
        updateRepositoryFiles(course)
    }

    override fun deactivate(course: Course) = Unit

    override fun refresh(course: Course) {
        updateRepository(course)
    }

    /**
     * Deletes course files and then downloads them again
     */
    fun reloadFiles(course: Course) {
        deleteBaseFiles(course)
        deleteSolutionFiles(course)
        updateRepositoryFiles(course)
    }

    private fun addRepository(course: Course) {
        gitplagClient.addRepository(RepositoryInput(
                git = defaultGit,
                language = toGitplagLanguage(course.language),
                name = "${course.githubUserId}/${course.name}",
                analyzer = defaultAnalyzer,
                filePatterns = listOf(course.settings.plagiarismFilePattern ?: course.language.buildExtensionRegexp()),
                analysisMode = defaultAnalysisMode
        )).callUnit()
    }

    private fun updateRepository(course: Course) {
        gitplagClient.updateRepository(
                github, course.githubUserId, course.name,
                RepositoryUpdate(
                        language = toGitplagLanguage(course.language),
                        analyzer = defaultAnalyzer,
                        filePatterns = listOf(course.settings.plagiarismFilePattern
                                ?: course.language.buildExtensionRegexp()),
                        analysisMode = defaultAnalysisMode
                )).callUnit()
    }

    private fun deleteBaseFiles(course: Course) = gitplagClient.deleteBaseFiles(
            vcsService = github,
            username = course.githubUserId,
            projectName = course.name
    ).callUnit()

    private fun deleteSolutionFiles(course: Course) = gitplagClient.deleteSolutionFiles(
            vcsService = github,
            username = course.githubUserId,
            projectName = course.name
    ).callUnit()

    private fun updateRepositoryFiles(course: Course) = gitplagClient.updateRepositoryFiles(
            vcsService = github,
            username = course.githubUserId,
            projectName = course.name
    ).callUnit()

    private fun <T> Call<T>.call(): Either<ResponseBody, T> =
            execute().run {
                if (isSuccessful) Either.right(body()!!)
                else Either.left(errorBody()!!)
            }

    private val Course.githubUserId: String
        get() = user.githubId ?: throw ModelException("Github id for ${user.name} user was not found")

    private val Course.language: Language
        get() = Language.from(settings.language)
                ?: throw UnsupportedLanguageException("Course $name does not have language property")

    private fun <T> Call<T>.callUnit(): ResponseBody? =
            execute().run { if (isSuccessful) null else errorBody() }

    private fun Language.buildExtensionRegexp() =
            """.+\.""" + extensions.joinToString(separator = "|", prefix = "(", postfix = ")")
}
