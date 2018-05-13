package org.flaxo.travis

/**
 * Travis repository interface.
 */
interface TravisRepository {

    /**
     * Travis repository id.
     */
    val id: Int

    /**
     * Travis repository name.
     *
     * It may differs with git repository name.
     */
    val name: String

    /**
     * Repository slug.
     *
     * Usually: githubUserId/repositoryName.
     */
    val slug: String

    /**
     * Activeness status of the travis validations for the current repository.
     */
    val active: Boolean

    /**
     * Git private repository flag.
     */
    val private: Boolean

    /**
     * Travis repository owner.
     */
    val owner: TravisUser
}