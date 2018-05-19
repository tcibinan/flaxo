package org.flaxo.travis.retrofit

/**
 * Travis api response repository POJO.
 */
class RetrofitTravisRepositoryPOJO {

    var id: Int = 0

    var name: String = ""

    var slug: String = ""

    var active: Boolean = true

    var private: Boolean = false

    lateinit var owner: RetrofitTravisUserPOJO
}