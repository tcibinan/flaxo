package org.flaxo.gradle

import org.flaxo.core.env.EnvironmentFile

class GradleBuildFile
private constructor(private val content: String)
    : EnvironmentFile {

    override val name = "build.gradle"

    override fun content() = content

    // temporary implementation
    override fun with(path: String): EnvironmentFile = this

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