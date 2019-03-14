package org.flaxo.rest.api

import org.apache.commons.collections4.map.PassiveExpiringMap
import org.apache.logging.log4j.LogManager
import org.flaxo.common.of
import org.flaxo.model.DataManager
import org.flaxo.model.TaskView
import org.flaxo.rest.manager.moss.MossManager
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
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Moss plagiarism analysis controller.
 */
@RestController
// TODO 14.03.19: Change endpoint to /rest/plagiarism.
@RequestMapping("/rest/moss")
class PlagiarismController(private val dataManager: DataManager,
                           private val responseManager: ResponseManager,
                           private val mossManager: MossManager
) {

    companion object {
        private val logger = LogManager.getLogger(CourseController::class.java)
    }

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
    ): Response<TaskView> {
        logger.info("Trying to start plagiarism analysis for ${principal.name}/$courseName")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val task = course.tasks.find { it.branch == taskBranch }
                ?: return responseManager.taskNotFound(principal.name, courseName, taskBranch)

        return responseManager.ok(mossManager.analysePlagiarism(task).view())
    }

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
    ): Response<String> {
        logger.info("Trying to generate plagiarism report graph access token for task" +
                "${principal.name}/$courseName/$taskBranch.")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(principal.name, courseName)

        val task = course.tasks.find { it.branch == taskBranch }
                ?: return responseManager.taskNotFound(principal.name, courseName, taskBranch)

        val analysis = task.plagiarismReports.lastOrNull()
                ?: return responseManager.notFound("No plagiarism report for task " +
                        "${principal.name}/${course.name}/${task.branch} was found.")

        val uuid = UUID.randomUUID().toString()
        analyses[uuid] = analysis.id
        return responseManager.ok(uuid)
    }

    /**
     * Retrieves a plagiarism report graph by the access token.
     *
     * @param token Plagiarism report graph access token.
     */
    @GetMapping("/graph/{token}")
    @Transactional
    fun getPlagiarismGraph(@PathVariable token: String): Any {
        // TODO 13.03.19: Move to PlagiarismService.
        logger.info("Trying to retrieve plagiarism graph by specified access token.")

        val analysisId = analyses[token]
                ?: return responseManager.notFound<Response<String>>("No plagiarism report was found " +
                        "for the specified access token.")

        val analysis = dataManager.getPlagiarismReport(analysisId)
                ?: return responseManager.notFound<Response<String>>("No plagiarism report found with id $analysisId.")

        // TODO 13.03.19: Extract plagiarism graph entity.
        val nodes = analysis.matches.asSequence()
                .map { it.student1 to it.student2 }
                .map { it.toList() }
                .flatMap { it.asSequence() }
                .distinct()
                .map {
                    object {
                        val name = it
                    }
                }
                .toList()
        val links = analysis.matches.map {
            object {
                val weight = it.percentage
                val first = it.student1
                val second = it.student2
            }
        }
        return object {
            val nodes = nodes
            val links = links
        }
    }
}
