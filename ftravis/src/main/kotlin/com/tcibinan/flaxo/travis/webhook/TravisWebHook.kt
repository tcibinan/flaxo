package com.tcibinan.flaxo.travis.webhook

class TravisWebHook {
    lateinit var status_message: String
    lateinit var type: String
    lateinit var branch: String
    lateinit var author_name: String
    lateinit var repository: TravisWebHookRepository
}

