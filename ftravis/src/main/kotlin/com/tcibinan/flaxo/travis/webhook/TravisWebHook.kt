package com.tcibinan.flaxo.travis.webhook

class TravisWebHook {
    lateinit var status_message: String
    lateinit var type: String
    lateinit var branch: String
    var pull_request_number: String? = null
    lateinit var repository: TravisWebHookRepository
}

