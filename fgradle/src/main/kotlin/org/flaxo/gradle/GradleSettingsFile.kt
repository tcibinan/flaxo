package org.flaxo.gradle

import org.flaxo.common.env.file.EnvironmentFile
import org.flaxo.common.env.file.LazyLocalEnvironmentFile
import org.flaxo.common.env.file.LocalFile
import java.nio.file.Path
import java.nio.file.Paths

internal class GradleSettingsFile(private val plugins: Set<GradlePlugin>,
                                  override val path: Path = Paths.get("settings.gradle")
) : EnvironmentFile {

    override val content: String by lazy {
        val pluginsManagement = plugins.map { it.pluginManagement }.filterNotNull()
        val repositories = pluginsManagement.flatMap { it.repositories }

        gradle {
            pluginManagement {
                repositories {
                    repositories.forEach { repository(it) }
                }
                resolutionStrategy {
                    eachPlugin {
                        plugins
                                .mapNotNull { plugin -> plugin.pluginManagement?.let { plugin to it.dependency } }
                                .forEach { (plugin, dependency) -> useDependency(plugin, dependency) }
                    }
                }
            }
        }
    }

    private fun GradleDsl.useDependency(plugin: GradlePlugin, dependency: GradleDependency) {
        put("if (requested.id.id == \"${plugin.id}\") {")
        put("useModule(\"${dependency.group}:${dependency.id}:\${requested.version}\")",
                nesting + 1)
        put("}")
    }

    override fun toLocalFile(directory: Path): LocalFile = LazyLocalEnvironmentFile(path, directory) {
        content.byteInputStream()
    }
}
