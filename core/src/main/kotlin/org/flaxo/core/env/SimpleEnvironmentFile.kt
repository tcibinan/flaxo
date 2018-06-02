package org.flaxo.core.env

import java.nio.file.Path
import java.nio.file.Paths

/**
 * In-memory environment file.
 */
class SimpleEnvironmentFile(override val path: Path,
                            override val content: String
) : EnvironmentFile {

    constructor(fileName: String, content: String) :
            this(Paths.get(fileName), content)

}