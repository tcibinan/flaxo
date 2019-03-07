package org.flaxo.github

import org.flaxo.git.Branch
import org.flaxo.git.Commit

/**
 * Github repository commit.
 */
class GithubCommit(override val sha: String,
                   override val branch: Branch,
                   val github: Github
) : Commit
