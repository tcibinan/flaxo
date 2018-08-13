package org.flaxo.travis.retrofit

import org.flaxo.travis.TravisUser

class RetrofitTravisUser(pojo: RetrofitTravisUserPOJO): TravisUser {

    override val id: String by lazy { pojo.id }

    override val login: String by lazy { pojo.login }

    override val isSyncing: Boolean by lazy { pojo.isSyncing }

    override val githubId: Int by lazy { pojo.githubId }
}