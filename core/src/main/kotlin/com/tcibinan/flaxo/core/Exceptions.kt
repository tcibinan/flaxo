package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.build.BuildTool
import com.tcibinan.flaxo.core.build.Dependency
import com.tcibinan.flaxo.core.build.Plugin

/**
 * Unsupported dependency exception.
 */
class UnsupportedDependencyException(dependency: Dependency, buildTool: BuildTool)
    : RuntimeException("${buildTool.name()} couldn't work with ${dependency::class} dependency type")

/**
 * Unsupported plugin exception.
 */
class UnsupportedPluginException(plugin: Plugin, buildTool: BuildTool)
    : RuntimeException("${buildTool.name()} couldn't work with ${plugin::class} plugin type")