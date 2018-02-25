package com.tcibinan.flaxo.rest.api

enum class ServerAnswer(val code: Int, val defaultAnswer: String? = null) {
    SERVER_ERROR(5000),

    USER_ALREADY_EXISTS(4000, "model.user.error.already.exists"),
    ANOTHER_USER_DATA(4001, "user.error.get.others.courses"),
    NO_GITHUB_KEY(4002, "operation.need.github.key"),

    USER_CREATED(2000, "model.user.success.created"),
    COURSES_LIST(2001),
    COURSE_CREATED(2002, "course.success.created"),
    SUPPORTED_LANGUAGES(2003),
    COURSE_DELETED(2004, "course.success.deleted"),
    COURSE_NOT_FOUND(2005, "course.not.found"),
    USER_NOT_FOUND(2006, "user.not.found"),
    STUDENTS_STATISTICS(2007),
    COURSE_COMPOSED(2008, "course.success.composed"),
    PLAGIARISM_ANALYSIS_SCHEDULED(2009, "plagiarism.analysis.scheduled"),

    HELLO_WORLD(1000, "greeting"),
    ECHO(1001),
    MANUAL_REDIRECT(1002)
}