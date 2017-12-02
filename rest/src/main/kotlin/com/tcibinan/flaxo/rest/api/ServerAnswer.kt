package com.tcibinan.flaxo.rest.api

enum class ServerAnswer(val code: Int) {
    SERVER_ERROR(5000),

    USER_ALREADY_EXISTS(4000),

    USER_CREATED(2000),

    HELLO_WORLD(1000),
    ECHO(1001)
}