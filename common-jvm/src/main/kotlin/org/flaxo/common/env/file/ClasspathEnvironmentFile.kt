package org.flaxo.common.env.file

import org.flaxo.common.NotFoundException
import java.nio.file.Path

/**
 * Environment file that is located under java classpath.
 *
 * The [classpathPath] can be a relative path to resources directory or a relative path within jar archive.
 */
class ClasspathEnvironmentFile(private val classpathPath: Path,
                                    override val path: Path = classpathPath) : EnvironmentFile {

    override val binaryContent: ByteArray by lazy {
        javaClass.classLoader.getResourceAsStream(classpathPath.toString())
                ?.use { it.buffered().readBytes() }
                ?: throw NotFoundException("Bundled path $classpathPath doesn't exist.")
    }

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory) {
        binaryContent.inputStream()
    }
}
