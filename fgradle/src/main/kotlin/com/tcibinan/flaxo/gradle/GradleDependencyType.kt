package com.tcibinan.flaxo.gradle

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