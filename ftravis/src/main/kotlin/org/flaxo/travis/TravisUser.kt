package org.flaxo.travis

/**
 * Travis user interface.
 */
interface TravisUser {

    /**
     * Travis user id.
     */
    val id: String

    /**
     * Travis user login.
     */
    val login: String

    /**
     * Travis user synchronisation status flag.
     *
     * If flag is false then it *does not* mean that
     * underlying travis synchronization processes has
     * finished.
     */
    val isSyncing: Boolean

    /**
     * Travis user github id.
     */
    val githubId: Int
}