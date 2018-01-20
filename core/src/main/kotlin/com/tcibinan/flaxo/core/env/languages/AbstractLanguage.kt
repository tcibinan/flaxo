package com.tcibinan.flaxo.core.env.languages

import com.tcibinan.flaxo.core.env.frameworks.TestingFramework

abstract class AbstractLanguage(
        private val name: String,
        private val suitableTestLanguages: Set<Language>,
        private val suitableTestingFrameworks: Set<TestingFramework>
) : Language {
    override fun name() = name

    override fun compatibleTestingLanguages(): Set<Language> = suitableTestLanguages + this

    override fun compatibleTestingFrameworks(): Set<TestingFramework> = suitableTestingFrameworks
}