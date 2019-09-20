package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.model.CourseStatisticsView
import org.flaxo.model.DataManager
import org.flaxo.model.data.views
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.flaxo.rest.manager.statistics.StatisticsManager
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Course statistics controller.
 */
@RestController
@RequestMapping("/rest/statistics")
class StatisticsController(private val dataManager: DataManager,
                           private val responseManager: ResponseManager,
                           private val statisticsManager: Map<String, StatisticsManager>
) {

    private val logger = LogManager.getLogger(StatisticsController::class.java)

    /**
     * Returns a downloadable [principal]'s [courseName] statistics
     * in the given [format].
     */
    @GetMapping("/download")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun downloadStatistics(@RequestParam courseName: String,
                           @RequestParam format: String,
                           principal: Principal,
                           request: HttpServletRequest,
                           response: HttpServletResponse
    ): ResponseEntity<Any> {
        logger.info("Collecting statistics of ${principal.name}/$courseName for download in $format format")

        val user = dataManager.getUser(principal.name)
                ?: return responseManager.userNotFound<String>(principal.name)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound<String>(principal.name, courseName)

        val statisticsConverter = statisticsManager[format]
                ?: return responseManager.bad<String>("Given $format format is not supported")

        return course.tasks
                .map { it.branch to it.solutions }
                .toMap()
                .mapValues { (_, solutions) ->
                    solutions.map { it.student.name to (it.score ?: 0) }
                            .toMap()
                }
                .let { statisticsConverter.convert(it) }
                .toByteArray()
                .let { statistics ->
                    ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment;filename=$courseName-statistics.${statisticsConverter.extension}"
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentLength(statistics.size.toLong())
                            .body(ByteArrayResource(statistics))
                }
    }

    /**
     * Returns all statistics of the course.
     *
     * @param ownerNickname Course owner nickname.
     * @param courseName Name of the course and related git repository.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun getCourseStatistics(@RequestParam("owner") ownerNickname: String,
                            @RequestParam("course") courseName: String
    ): Response<CourseStatisticsView> {
        logger.info("Aggregating course $ownerNickname/$courseName statistics")

        val user = dataManager.getUser(ownerNickname)
                ?: return responseManager.userNotFound(ownerNickname)

        val course = dataManager.getCourse(courseName, user)
                ?: return responseManager.courseNotFound(ownerNickname, courseName)

        return responseManager.ok(CourseStatisticsView(course.tasks.views()))
    }
}
