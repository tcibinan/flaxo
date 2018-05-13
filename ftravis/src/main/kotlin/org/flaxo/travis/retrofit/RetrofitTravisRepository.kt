package org.flaxo.travis.retrofit

import org.flaxo.travis.TravisRepository
import org.flaxo.travis.TravisUser

class RetrofitTravisRepository(pojo: RetrofitTravisRepositoryPOJO)
    : TravisRepository {

    override val id: Int by lazy { pojo.id }

    override val name: String by lazy { pojo.name }

    override val slug: String by lazy { pojo.slug }

    override val active: Boolean by lazy { pojo.active }

    override val private: Boolean by lazy { pojo.private }

    override val owner: TravisUser by lazy { RetrofitTravisUser(pojo.owner) }
}