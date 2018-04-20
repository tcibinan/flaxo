package org.flaxo.rest.service.travis

import org.flaxo.cmd.CmdExecutor
import org.flaxo.travis.SimpleTravis
import org.flaxo.travis.Travis
import org.flaxo.travis.TravisClient
import org.flaxo.travis.build.TravisBuild
import org.flaxo.travis.parseTravisWebHook
import java.io.Reader

/**
 * Travis service basic implementation.
 */
class TravisSimpleService(private val client: TravisClient)
    : TravisService {

    override fun retrieveTravisToken(githubUsername: String, githubToken: String): String {
        CmdExecutor.execute("travis", "login",
                "-u", githubUsername,
                "-g", githubToken)

        return CmdExecutor.execute("travis", "token")
                .first().split(" ").last()
    }

    override fun travis(travisToken: String): Travis =
            SimpleTravis(client, travisToken)

    override fun parsePayload(reader: Reader): TravisBuild? =
            parseTravisWebHook(reader)

}
