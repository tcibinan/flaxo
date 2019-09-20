package org.flaxo.rest.manager.travis

import org.flaxo.common.cmd.CmdExecutor

class TravisTokenSupplier {
    fun supply(githubUsername: String, githubToken: String): String = with(CmdExecutor) {
        execute("travis", "login", "--com", "-u", githubUsername, "-g", githubToken)
        execute("travis", "token", "--com").first().split(" ").last()
    }
}
