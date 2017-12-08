package com.tcibinan.flaxo.core.frameworks

abstract class TestingFramework(val name: String)

object JUnit4TestingFramework : TestingFramework("junit4")
object JUnit5TestingFramework : TestingFramework("junit5")
object SpekTestingFramework : TestingFramework("spek")