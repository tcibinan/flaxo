package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.git.BranchInstance
import com.tcibinan.flaxo.git.GitInstance

fun GitInstance.createCourse(
        courseName: String,
        environment: Environment,
        numberOfTasks: Int
) {
    createRepository(courseName)
            .createBranch("prerequisites")
            .fillWith(environment)
            .createSubBranches(numberOfTasks, "task-")
}

fun BranchInstance.fillWith(environment: Environment): BranchInstance {
    environment.getFiles()
            .forEach { load(it.name(), it.content()) }
    return this
}