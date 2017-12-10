package com.tcibinan.flaxo.rest.services

import com.tcibinan.flaxo.core.env.Environment
import com.tcibinan.flaxo.core.env.frameworks.TestingFramework
import com.tcibinan.flaxo.core.env.languages.Language
import com.tcibinan.flaxo.core.env.tools.BuildTool
import com.tcibinan.flaxo.rest.NoDefaultBuildTool
import com.tcibinan.flaxo.rest.IncompatibleLanguage
import com.tcibinan.flaxo.rest.IncompatibleTestingFramework
import com.tcibinan.flaxo.rest.UnsupportedLanguage
import com.tcibinan.flaxo.rest.UnsupportedTestingFramework

class SimpleRepositoryEnvironmentService(
        val languages: Map<String, Language>,
        val testingFrameworks: Map<String, TestingFramework>,
        val defaultBuildTools: Map<Language, () -> BuildTool>
) : RepositoryEnvironmentService {

    override fun produceEnvironment(
            languageName: String,
            testingLanguageName: String,
            testingFrameworkName: String
    ): Environment {
        val language = languages[languageName]
                ?: throw UnsupportedLanguage(languageName)
        val testingLanguage = languages[testingLanguageName]
                ?: throw UnsupportedLanguage(testingLanguageName)
        val testingFramework = testingFrameworks[testingFrameworkName]
                ?: throw UnsupportedTestingFramework(testingFrameworkName)

        testingLanguage.shouldSuited(language)
        testingFramework.shouldSuited(testingLanguage)

        val buildTool = (defaultBuildTools[language] ?: throw NoDefaultBuildTool(language)).invoke()

        language.main(buildTool)
        testingLanguage.test(buildTool)
        testingFramework.test(buildTool)

        return buildTool.buildEnvironment()
    }

}

private fun TestingFramework.shouldSuited(testingLanguage: Language) {
    if (!testingLanguage.worksWith(this)) {
        throw IncompatibleTestingFramework(this, testingLanguage)
    }
}

private fun Language.shouldSuited(language: Language) {
    if (!language.canBeTestedBy(this)) {
        throw IncompatibleLanguage(language, this)
    }
}