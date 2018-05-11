package org.flaxo.travis

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Travis retrofit client.
 */
interface TravisClient {

    /**
     * Retrieves associated travis user.
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @GET("user")
    fun getUser(@Header("Authorization") authorization: String
    ): Call<TravisUser>

    /**
     * Retrieves repository by [repositorySlug].
     *
     * Repository slug is usually one of the following:
     * - username/repository
     * - username%2Frepository
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @GET("/repo/{repository_slug}")
    fun getRepository(@Header("Authorization") authorization: String,
                      @Path("repository_slug") repositorySlug: String
    ): Call<TravisRepository>

    /**
     * Activates repository by [repositorySlug].
     *
     * Repository slug is usually one of the following:
     * - username/repository
     * - username%2Frepository
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @POST("/repo/{repository_slug}/activate")
    fun activate(@Header("Authorization") authorization: String,
                 @Path("repository_slug") repositorySlug: String
    ): Call<TravisRepository>

    /**
     * Deactivates repository by [repositorySlug].
     *
     * Repository slug is usually one of the following:
     * - username/repository
     * - username%2Frepository
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @POST("/repo/{repository_slug}/deactivate")
    fun deactivate(@Header("Authorization") authorization: String,
                   @Path("repository_slug") repositorySlug: String
    ): Call<TravisRepository>

    /**
     * Triggers sync for travis user by id [travisUserId].
     *
     * Repository slug is usually one of the following:
     * - username/repository
     * - username%2Frepository
     *
     * Also requires travis authorization token.
     */
    @Headers("Travis-API-Version: 3")
    @POST("/user/{travis_user_id}/sync")
    fun sync(@Header("Authorization") authorization: String,
             @Path("travis_user_id") travisUserId: String
    ): Call<ResponseBody>

}

