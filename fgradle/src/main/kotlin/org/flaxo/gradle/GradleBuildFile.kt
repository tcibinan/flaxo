package org.flaxo.gradle

import org.flaxo.core.env.file.EnvironmentFile
import java.nio.file.Path
import java.nio.file.Paths

internal class GradleBuildFile(private val plugins: Set<GradlePlugin>,
                               private val repositories: Set<GradleRepository>,
                               private val dependencies: Set<GradleDependency>,
                               override val path: Path = Paths.get("build.gradle")
) : EnvironmentFile {

    override val content: String by lazy {
        gradle {
            plugins {
                plugins.forEach { plugin(it) }
            }

            repositories {
                repositories.forEach { repository(it) }
            }

            dependencies {
                dependencies.forEach { dependency(it) }
            }
        }
    }

    private fun GradleDsl.plugin(plugin: GradlePlugin) {
        val versionSubstring = plugin.version?.let { "version \"$it\"" } ?: ""
        put("id \"${plugin.id}\" $versionSubstring")
    }

}