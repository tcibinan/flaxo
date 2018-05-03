package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataService
import org.flaxo.rest.service.converter.StatisticsConverter
import org.flaxo.rest.service.response.ResponseService
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

@RestController
@RequestMapping("rest/statistics")
class StatisticsController(private val dataService: DataService,
                           private val responseService: ResponseService,
                           private val statisticsConverters: Map<String, StatisticsConverter>
) {

    private val logger = LogManager.getLogger(ModelController::class.java)

    @GetMapping("/download")
    @PreAuthorize("hasAuthority('USER')")
    @Transactional(readOnly = true)
    fun downloadStatistics(@RequestParam
                           courseName: String,
                           @RequestParam
                           format: String,
                           principal: Principal,
                           request: HttpServletRequest,
                           response: HttpServletResponse
    ): ResponseEntity<Any> {
        logger.info("Collecting statistics of ${principal.name}/$courseName for download in $format format")

        val user = dataService.getUser(principal.name)
                ?: return responseService.userNotFound(principal.name)

        val course = dataService.getCourse(courseName, user)
                ?: return responseService.courseNotFound(principal.name, courseName)

        val statisticsConverter = statisticsConverters[format]
                ?: return responseService.bad("Given $format format is not supported")

        return course.tasks
                .map { it.branch to it.solutions }
                .toMap()
                .mapValues { (_, solutions) ->
                    solutions.map { it.student.nickname to (it.score ?: 0) }
                            .toMap()
                }
                .let { statisticsConverter.convert(it) }
                .toByteArray()
                .let { statistics ->
                    ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION,
                                    "attachment;filename=$courseName-statistics.json"
                            )
                            .contentType(MediaType.APPLICATION_JSON)
                            .contentLength(statistics.size.toLong())
                            .body(ByteArrayResource(statistics))
                }
    }
}