package com.tcibinan.flaxo.core.language

import com.tcibinan.flaxo.core.framework.TestingFramework

abstract class AbstractLanguage(
        private val name: String,
        override val extension: String,
        private val suitableTestLanguages: Set<Language>,
        private val suitableTestingFrameworks: Set<TestingFramework>
) : Language {

    override fun name() = name

    override fun toString() = name()

    override fun compatibleTestingLanguages(): Set<Language> = suitableTestLanguages + this

    override fun compatibleTestingFrameworks(): Set<TestingFramework> = suitableTestingFrameworks
}