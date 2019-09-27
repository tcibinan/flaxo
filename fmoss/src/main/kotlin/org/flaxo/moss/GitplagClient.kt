package org.flaxo.moss

import io.gitplag.gitplagapi.model.input.AnalysisRequest
import io.gitplag.gitplagapi.model.output.analysis.AnalysisResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Gitplag retrofit client.
 */
interface GitplagClient {

    /**
     * Analyzes a task of a course.
     */
    @POST("/api/repositories/{vcsService}/{username}/{projectName}/analyze")
    fun analyse(@Path("vcsService")
                vcsService: String,
                @Path("username")
                username: String,
                @Path("projectName")
                projectName: String,
                @Body
                analysisRequest: AnalysisRequest
    ): Call<AnalysisResult>

}

