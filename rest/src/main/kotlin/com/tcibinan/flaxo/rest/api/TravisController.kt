package com.tcibinan.flaxo.rest.api

import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.data.StudentTask
import com.tcibinan.flaxo.rest.service.travis.TravisService
import com.tcibinan.flaxo.travis.build.BuildStatus
import com.tcibinan.flaxo.travis.build.TravisPullRequestBuild
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping("/rest/travis")
class TravisController @Autowired constructor(private val travisService: TravisService,
                                              private val dataService: DataService
) {

    @PostMapping("/hook")
    fun travisWebHook(request: HttpServletRequest) {
        val hook = travisService.parsePayload(request.reader)
                ?: throw UnsupportedOperationException("Unsupported travis web hook type")

        when (hook) {
            is TravisPullRequestBuild ->
                when (hook.status) {
                    BuildStatus.SUCCEED ->
                        dataService.updateStudentTask(getStudentTaskBy(hook).with(
                                anyBuilds = true,
                                buildSucceed = true
                        ))
                    BuildStatus.FAILED ->
                        dataService.updateStudentTask(getStudentTaskBy(hook).with(
                                anyBuilds = true,
                                buildSucceed = false
                        ))
                    BuildStatus.IN_PROGRESS -> {
                        // ignore
                    }
                    else -> {
                        // do nothing
                    }
                }
        }

    }

    private fun getStudentTaskBy(hook: TravisPullRequestBuild): StudentTask {
        val user = dataService.getUser(hook.repositoryOwner)
                ?: throw Exception("User with the required nickname ${hook.repositoryOwner} wasn't found.")

        val course = dataService.getCourse(hook.repositoryName, user)
                ?: throw Exception("Course with name ${hook.repositoryName} wasn't found " +
                        "for user ${user.nickname}.")

        val student = course.students
                .find { it.nickname == hook.author }
                ?: throw Exception("Student ${hook.author} wasn't found " +
                        "in course ${hook.repositoryOwner}/${hook.repositoryName}.")

        return student.studentTasks
                .find { it.task.name == hook.branch }
                ?: throw Exception("Student task ${hook.branch} wasn't found for student ${hook.author} " +
                        "in course ${hook.repositoryOwner}/${hook.repositoryName}.")
    }
}