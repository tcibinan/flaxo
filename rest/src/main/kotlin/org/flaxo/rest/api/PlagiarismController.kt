package org.flaxo.rest.api

import org.apache.commons.collections4.map.PassiveExpiringMap
import org.flaxo.common.data.PlagiarismReport
import org.flaxo.common.data.plagiarism.PlagiarismGraph
import org.flaxo.common.of
import org.flaxo.rest.manager.plag.PlagiarismManager
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.util.concurrent.TimeUnit

/**
 * Moss plagiarism analysis controller.
 */
@RestController
@RequestMapping("/rest/plagiarism")
class PlagiarismController(private val plagiarismManager: PlagiarismManager,
                           private val responseManager: ResponseManager) {
    /**
     * Map of random tokens to plagiarism analysis id.
     */
    private val analyses: MutableMap<String, Long> = PassiveExpiringMap(1 of TimeUnit.HOURS)

    /**
     * Performs task plagiarism analysis.
     *
     * @param courseName Name of the course to find task into.
     * @param taskBranch Name of the task branch to perform plagiarism analysis for.
     */
    @PostMapping("/analyse")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun analysePlagiarism(@RequestParam courseName: String,
                          @RequestParam taskBranch: String,
                          principal: Principal
    ): Response<PlagiarismReport> = responseManager.ok(plagiarismManager.analyse(principal.name, courseName, taskBranch))

    /**
     * Generates task latest plagiarism report graph temporary access token.
     *
     * @param courseName Name of the course to find task into.
     * @param taskBranch Name of the task branch to find plagiarism report to generate token for.
     */
    @PostMapping("/graph/token")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional
    fun generatePlagiarismGraphAccessToken(@RequestParam courseName: String,
                                           @RequestParam taskBranch: String,
                                           principal: Principal
    ): Response<String> =
            responseManager.ok(plagiarismManager.generateGraphAccessToken(principal.name, courseName, taskBranch))

    /**
     * Retrieves a plagiarism report graph by the access token.
     *
     * @param token Plagiarism report graph access token.
     */
    @GetMapping("/graph/{token}")
    @Transactional
    fun getPlagiarismGraph(@PathVariable token: String): PlagiarismGraph = plagiarismManager.getGraph(token)

}
