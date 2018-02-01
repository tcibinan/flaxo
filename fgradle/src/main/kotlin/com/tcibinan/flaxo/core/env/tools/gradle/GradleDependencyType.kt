package com.tcibinan.flaxo.core.env.tools.gradle

enum class GradleDependencyType(
        private val type: String
) {
    COMPILE("compile"),
    TEST_COMPILE("testCompile"),
    TEST_RUNTIME("testRuntime"),
    CLASSPATH("classpath"),
    COMPILE_KOTLIN("compileKotlin");

    override fun toString() = type
}