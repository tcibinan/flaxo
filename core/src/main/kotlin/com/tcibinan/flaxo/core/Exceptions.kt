package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.core.env.languages.Language
import com.tcibinan.flaxo.core.env.tools.BuildToolPlugin
import com.tcibinan.flaxo.core.env.tools.Dependency

class EntityAlreadyExistsException(val entity: String) : Exception(entity + "already exists")

class UnsupportedBuildToolException(val language: Language, val buildTool: BuildTool)
    : Exception("${language.name} couldn't work with ${buildTool.name()}")

class UnsupportedDependencyException(val dependency: Dependency, val buildTool: BuildTool)
    : Exception("${buildTool.name()} couldn't work with ${dependency::class} dependency type")

class UnsupportedPluginException(val plugin: BuildToolPlugin, val buildTool: BuildTool)
    : Exception("${buildTool.name()} couldn't work with ${plugin::class} plugin type")