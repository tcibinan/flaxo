package org.flaxo.rest

import org.flaxo.model.data.Course
import org.flaxo.moss.MossSubmission

val Course.friendlyId
    get() = "${user.nickname}/$name"

val MossSubmission.friendlyId
    get() = "$user/$course/$branch"
