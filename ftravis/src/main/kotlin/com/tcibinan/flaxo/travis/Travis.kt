package com.tcibinan.flaxo.travis

import io.vavr.control.Either
import okhttp3.ResponseBody

interface Travis {
    fun getUser(): Either<ResponseBody, TravisUser>
    fun activate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository>
    fun deactivate(userName: String, repositoryName: String): Either<ResponseBody, TravisRepository>
}

