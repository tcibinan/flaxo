package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.framework.TestingFramework
import com.tcibinan.flaxo.core.language.Language

class UnsupportedTestingFramework(testingFrameworkName: String)
    : Exception("$testingFrameworkName is not supported")

class UnsupportedLanguage(languageName: String)
    : Exception("$languageName is not supported")

class NoDefaultBuildTool(language: Language)
    : Exception("No default build tool for $language")

class IncompatibleTestingFramework(testingFramework: TestingFramework, testingLanguage: Language)
    : Exception("$testingLanguage doesn't support $testingFramework as testing framework")

class IncompatibleLanguage(language: Language, testingLanguage: Language)
    : Exception("$language doesn't support $testingLanguage as language for tests")