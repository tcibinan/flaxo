package org.flaxo.codacy

import okhttp3.ResponseBody
import org.flaxo.codacy.request.ProjectRequest
import org.flaxo.codacy.response.CommitDeltaResponse
import org.flaxo.codacy.response.CommitDetailsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Codacy retrofit client.
 */
interface CodacyClient {

    /**
     * Retrieves commit details.
     *
     * Also requires codacy [apiToken].
     */
    @GET("{username}/{projectName}/commit/{commitUuid}")
    fun commitDetails(@Path("username")
                      username: String,
                      @Path("projectName")
                      projectName: String,
                      @Path("commitUuid")
                      commitUuid: String,
                      @Header("api_token")
                      apiToken: String
    ): Call<CommitDetailsResponse>

    /**
     * Retrieves commit [commitUuid] delta.
     *
     * Also requires codacy [apiToken].
     */
    @GET("{username}/{projectName}/commit/{commitUuid}/delta")
    fun commitDelta(@Path("username")
                    username: String,
                    @Path("projectName")
                    projectName: String,
                    @Path("commitUuid")
                    commitUuid: String,
                    @Header("api_token")
                    apiToken: String
    ): Call<CommitDeltaResponse>

    /**
     * Creates a public project.
     *
     * Also requires codacy [apiToken].
     */
    @POST("project/create/public")
    fun createPublicProject(@Body
                            projectRequest: ProjectRequest,
                            @Header("api_token")
                            apiToken: String
    ): Call<ResponseBody>

    /**
     * Deletes a [projectName] from codacy.
     *
     * Also requires codacy [apiToken].
     */
    @POST("{username}/{projectName}/delete")
    fun deleteProject(@Path("username")
                      username: String,
                      @Path("projectName")
                      projectName: String,
                      @Header("api_token")
                      apiToken: String
    ): Call<ResponseBody>

}

