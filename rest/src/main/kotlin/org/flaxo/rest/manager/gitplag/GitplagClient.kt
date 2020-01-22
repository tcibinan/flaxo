package org.flaxo.rest.manager.gitplag

import io.gitplag.gitplagapi.model.input.AnalysisRequest
import io.gitplag.gitplagapi.model.input.RepositoryInput
import io.gitplag.gitplagapi.model.input.RepositoryUpdate
import io.gitplag.gitplagapi.model.output.analysis.AnalysisResult
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Gitplag retrofit client.
 */
interface GitplagClient {

    /**
     * Analyzes a task of a course.
     */
    @POST("/api/repositories/{vcsService}/{username}/{projectName}/analyze")
    fun analyse(@Path("vcsService") vcsService: String,
                @Path("username") username: String,
                @Path("projectName") projectName: String,
                @Body analysisRequest: AnalysisRequest
    ): Call<AnalysisResult>

    /**
     * Adds a repository to Gitplag.
     */
    @POST("/api/repositories")
    fun addRepository(@Body repository: RepositoryInput): Call<ResponseBody>

    /**
     * Updates the repository in Gitplag.
     */
    @PUT("/api/repositories/{vcsService}/{username}/{projectName}")
    fun updateRepository(@Path("vcsService") vcsService: String,
                         @Path("username") username: String,
                         @Path("projectName") projectName: String,
                         @Body repository: RepositoryUpdate): Call<ResponseBody>

    /**
     * Updates files of a repository in Gitplag.
     */
    @GET("/api/repositories/{vcsService}/{username}/{projectName}/files/update/detached")
    fun updateRepositoryFiles(@Path("vcsService") vcsService: String,
                              @Path("username") username: String,
                              @Path("projectName") projectName: String): Call<ResponseBody>

    /**
     * Deletes base files of a repository in Gitplag.
     */
    @DELETE("/api/repositories/{vcsService}/{username}/{projectName}/bases/delete")
    fun deleteBaseFiles(@Path("vcsService") vcsService: String,
                        @Path("username") username: String,
                        @Path("projectName") projectName: String): Call<ResponseBody>

    /**
     * Deletes solution files of a repository in Gitplag.
     */
    @DELETE("/api/repositories/{vcsService}/{username}/{projectName}/solutions/delete")
    fun deleteSolutionFiles(@Path("vcsService") vcsService: String,
                            @Path("username") username: String,
                            @Path("projectName") projectName: String): Call<ResponseBody>

}

