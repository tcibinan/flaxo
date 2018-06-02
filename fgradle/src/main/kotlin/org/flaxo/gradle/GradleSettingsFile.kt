package org.flaxo.gradle

import org.flaxo.core.env.EnvironmentFile
import java.nio.file.Path
import java.nio.file.Paths

class GradleSettingsFile private constructor(override val content: String)
    : EnvironmentFile {

    override val path: Path = Paths.get("settings.gradle")

    companion object {

        fun with(plugins: Set<GradlePlugin>): GradleSettingsFile {
            val pluginsManagement = plugins.map { it.pluginManagement }.filterNotNull()
            val repositories = pluginsManagement.flatMap { it.repositories }

            val content =
                    gradle {
                        pluginManagement {
                            repositories {
                                repositories.forEach { repository(it) }
                            }
                            resolutionStrategy {
                                eachPlugin {
                                    plugins
                                            .mapNotNull { plugin ->
                                                plugin.pluginManagement
                                                        ?.let { plugin to it.dependency }
                                            }
                                            .forEach { (plugin, dependency) ->
                                                put("if (requested.id.id == \"${plugin.id}\") {")
                                                put("useModule(\"${dependency.group}:${dependency.id}:\${requested.version}\")",
                                                        nesting + 1)
                                                put("}")
                                            }
                                }
                            }
                        }
                    }

            return GradleSettingsFile(content)
        }
    }
}