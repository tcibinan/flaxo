package org.flaxo.rest.manager.travis

import org.flaxo.rest.manager.ValidationManager
import org.flaxo.travis.TravisBuild
import java.io.Reader

/**
 * Travis manager.
 */
interface TravisManager : ValidationManager {

    /**
     * Parses travis build webhook payload from [reader].
     */
    fun parsePayload(reader: Reader): TravisBuild?

}
