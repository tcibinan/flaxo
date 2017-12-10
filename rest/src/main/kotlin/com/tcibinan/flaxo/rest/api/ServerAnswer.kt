package com.tcibinan.flaxo.rest.api

enum class ServerAnswer(val code: Int) {
    SERVER_ERROR(5000),

    USER_ALREADY_EXISTS(4000),
    ANOTHER_USER_DATA(4001),
    NO_GITHUB_KEY(4002),

    USER_CREATED(2000),
    COURSES_LIST(2001),
    COURSE_CREATED(2002),
    SUPPORTED_LANGUAGES(2003),

    HELLO_WORLD(1000),
    ECHO(1001)
}