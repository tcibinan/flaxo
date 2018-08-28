package org.flaxo.git

/**
 * Git service provider client.
 *
 * Client is associated with a single user.
 */
interface Git {

    /**
     * @return git service user nickname.
     */
    fun nickname(): String

    /**
     * Creates a git repository for the current user.
     *
     * @param repositoryName Git service repository name.
     * @param private Type of the repository private/public to create *(public by default)*.
     * @return Repository of the current user with [repositoryName] and [private] type.
     */
    fun createRepository(repositoryName: String,
                         private: Boolean = false
    ): Repository

    /**
     * Forks existing repository of [ownerNickname] user with [repositoryName].
     *
     * @return Forked repository.
     */
    fun forkRepository(ownerNickname: String,
                       repositoryName: String
    ): Repository

    /**
     * Returns a [repositoryName] repository of a current user.
     *
     * @return Repository of current user with [repositoryName].
     */
    fun getRepository(repositoryName: String): Repository

    /**
     * Returns a [repositoryName] repository of [ownerName] user.
     *
     * @return Repository of [ownerName] user with [repositoryName].
     */
    fun getRepository(ownerName: String,
                      repositoryName: String
    ): Repository

}