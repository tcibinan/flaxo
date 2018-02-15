package com.tcibinan.flaxo.rest.service.travis

class Travis(private val travisClient: TravisClient,
             private val travisToken: String) {

    fun getUser() = travisClient.getUser(authorization()).execute().body()

    fun activate(repositoryId: String) =
            travisClient.activate(authorization(), repositoryId).execute().body()

    fun deactivate(repositoryId: String) =
            travisClient.deactivate(authorization(), repositoryId).execute().body()

    private fun authorization() = "token $travisToken"

}