package org.flaxo.rest.api

import org.apache.logging.log4j.LogManager
import org.flaxo.model.DataManager
import org.flaxo.model.TaskView
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.response.Response
import org.flaxo.rest.manager.response.ResponseManager
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/rest/moss")
class MossController(private val dataManager: DataManager,
                     private val responseManager: ResponseManager,
                     private val mossManager: MossManager
) {

    companion object {
        private val logger = LogManager.getLogger(CourseController::class.java)
    }

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
}
