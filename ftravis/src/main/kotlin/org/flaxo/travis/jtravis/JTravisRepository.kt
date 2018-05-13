package org.flaxo.travis.jtravis

import fr.inria.jtravis.entities.Repository
import org.flaxo.travis.TravisRepository
import org.flaxo.travis.TravisUser

class JTravisRepository(repository: Repository)
    : TravisRepository {

    override val id: Int = repository.id

    override val name: String = repository.name

    override val slug: String = repository.slug

    override val active: Boolean = repository.isActive

    override val private: Boolean = repository.isPrivateProperty

    override val owner: TravisUser = JTravisUser(repository.owner)
}