package com.tcibinan.flaxo.travis.webhook

/**
 * Travis web hook class.
 *
 * Uses to map json to object model.
 */
class TravisWebHook {
    lateinit var status_message: String
    lateinit var type: String
    lateinit var branch: String
    var pull_request_number: String? = null
    lateinit var repository: TravisWebHookRepository
}

