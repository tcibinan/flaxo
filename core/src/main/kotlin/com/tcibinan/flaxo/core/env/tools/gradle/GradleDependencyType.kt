package com.tcibinan.flaxo.core.env.tools.gradle

enum class GradleDependencyType(
        val type: String
) {
    COMPILE("compile"),
    COMPILE_TEST("compileTest"),
    COMPILE_KOTLIN("compileKotlin");

    override fun toString() = type
}