package org.flaxo.rest

import org.flaxo.model.data.Course
import org.flaxo.model.data.Task

val Course.friendlyId
    get() = "${user.name}/$name"

val Task.friendlyId
    get() = "${course.friendlyId}/$branch"
