package org.flaxo.codacy

import arrow.core.Either
import okhttp3.ResponseBody
import org.flaxo.codacy.response.CommitDetailsResponse

/**
 * Codacy client interface.
 *
 * Client is associated with a single user.
 */
interface Codacy {

    /**
     * Retrieves details of [commitId] of the repository with [projectName].
     *
     * @param projectName Name of the project to retrieve commit from.
     * @param commitId Commit uuid to retrieve details about.
     * @return Either commit details or error body.
     */
    fun commitDetails(projectName: String,
                      commitId: String
    ): Either<ResponseBody, CommitDetailsResponse>

    /**
     * Creates project with [projectName] which locates by [repositoryUrl].
     *
     * @param projectName Name of the codacy project to create.
     * @param repositoryUrl Repository of the existing course.
     * @return Null or error response body if something went bad during
     * call to codacy api.
     */
    fun createProject(projectName: String,
                      repositoryUrl: String
    ): ResponseBody?

    /**
     * Deletes project from the codacy by [projectName].
     *
     * @param projectName Name of the codacy project to delete.
     * @return Null or error response body if something went bad during
     * call to codacy api.
     */
    fun deleteProject(projectName: String
    ): ResponseBody?
}