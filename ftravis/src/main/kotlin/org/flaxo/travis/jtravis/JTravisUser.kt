package org.flaxo.travis.jtravis

import fr.inria.jtravis.entities.Owner
import org.flaxo.travis.TravisUser

class JTravisUser(owner: Owner) : TravisUser {

    override val id: String = owner.id.toString()

    override val login: String = owner.login

    override val name: String = owner.name

    override val isSyncing: Boolean = owner.isSyncing

    override val githubId: Int = owner.githubId
}