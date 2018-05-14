package org.flaxo.travis.retrofit

import okhttp3.ResponseBody
import org.flaxo.travis.retrofit.RetrofitTravisBuildsPOJO
import org.flaxo.travis.retrofit.RetrofitTravisRepositoryPOJO
import org.flaxo.travis.retrofit.RetrofitTravisUserPOJO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
    ): Call<RetrofitTravisUserPOJO>

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
    ): Call<RetrofitTravisRepositoryPOJO>

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
    ): Call<RetrofitTravisRepositoryPOJO>

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
    ): Call<RetrofitTravisRepositoryPOJO>

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

    /**
     * Retrieves builds for the repository by [repositorySlug].
     *
     * Filters builds by the given [eventType] and supports pagination
     * through [offset] builds and page [limit].
     */
    @Headers("Travis-API-Version: 3")
    @GET("/repo/{repository_slug}/builds")
    fun getBuilds(@Header("Authorization") authorization: String,
                  @Path("repository_slug") repositorySlug: String,
                  @Query("event_type") eventType: String? = null,
                  @Query("offset") offset: Int = 0,
                  @Query("limit") limit: Int = 25
    ): Call<RetrofitTravisBuildsPOJO>

}

