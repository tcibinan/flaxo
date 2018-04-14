package org.flaxo.gradle

import org.flaxo.core.env.EnvironmentFile

class GradleSettingsFile private constructor(private val content: String)
    : EnvironmentFile {

    override val name = "settings.gradle"

    override fun content(): String = content

    // temporary implementation
    override fun with(path: String): EnvironmentFile = this

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
                                    plugins.map { it to it.pluginManagement }
                                            .filter { it.second != null }
                                            .map { it as Pair<GradlePlugin, GradlePluginManagement> }
                                            .map { it.first to it.second.dependency }
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