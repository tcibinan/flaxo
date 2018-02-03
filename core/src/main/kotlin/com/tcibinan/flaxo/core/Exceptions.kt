package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.build.BuildToolPlugin
import com.tcibinan.flaxo.core.build.Dependency

class UnsupportedDependencyException(dependency: Dependency, buildTool: BuildTool)
    : Exception("${buildTool.name()} couldn't work with ${dependency::class} dependency type")

class UnsupportedPluginException(plugin: BuildToolPlugin, buildTool: BuildTool)
    : Exception("${buildTool.name()} couldn't work with ${plugin::class} plugin type")