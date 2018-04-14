package com.tcibinan.flaxo.gradle

data class GradlePluginManagement(val id: String,
                                  val dependency: GradleDependency,
                                  val repositories: Set<GradleRepository>)