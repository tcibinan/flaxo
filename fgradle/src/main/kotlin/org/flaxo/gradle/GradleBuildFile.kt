package org.flaxo.gradle

import org.flaxo.core.env.EnvironmentFile
import java.nio.file.Path
import java.nio.file.Paths

class GradleBuildFile private constructor(override val content: String)
    : EnvironmentFile {

    override val path: Path = Paths.get("build.gradle")

    companion object {

        fun with(plugins: Set<GradlePlugin>,
                 repositories: Set<GradleRepository>,
                 dependencies: Set<GradleDependency>
        ): GradleBuildFile {
            val content =
                    gradle {
                        plugins {
                            plugins.forEach {
                                val versionSubstring = it.version?.let { "version \"$it\"" } ?: ""
                                put("id \"${it.id}\" $versionSubstring")
                            }
                        }

                        repositories {
                            repositories.forEach { repository(it) }
                        }

                        dependencies {
                            dependencies.forEach { dependency(it) }
                        }
                    }

            return GradleBuildFile(content)
        }
    }
}