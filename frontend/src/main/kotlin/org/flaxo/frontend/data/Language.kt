package org.flaxo.frontend.data

class Language(val name: String,
               val compatibleTestingLanguages: List<Language>,
               val compatibleTestingFrameworks: List<TestingFramework>)
