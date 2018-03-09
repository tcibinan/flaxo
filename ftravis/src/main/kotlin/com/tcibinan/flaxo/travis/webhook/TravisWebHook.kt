package com.tcibinan.flaxo.travis.webhook

/**
 * Travis web hook class.
 *
 * Uses to map json to object model.
 */
class TravisWebHook {
    var status_message: String = ""
    var type: String = ""
    var branch: String = ""
    var pull_request_number: String? = null
    lateinit var repository: TravisWebHookRepository
}

