package org.flaxo.common

/**
 * Programming languages enum.
 */
enum class Language(

        override val alias: String,

        /**
         * Extensions of the files written on the language.
         */
        val extensions: Set<String>,

        /**
         * Compatible testing languages names.
         *
         * As long as enum instances can't be referred while their initiation it has to be specified as strings.
         */
        private val testingLanguageNames: Set<String> = emptySet(),

        /**
         * Compatible testing frameworks that can be used for writing tests with the language.
         */
        val testingFrameworks: Set<Framework> = emptySet()
) : Named, Aliased {
    Kotlin(Names.KOTLIN, setOf("kt", "kts"),
            testingLanguageNames = setOf(Names.KOTLIN),
            testingFrameworks = setOf(Framework.JUnit, Framework.Spek)),
    Java(Names.JAVA, setOf("java"),
            testingLanguageNames = setOf(Names.JAVA, Names.KOTLIN),
            testingFrameworks = setOf(Framework.JUnit)),
    Bash(Names.BASH, setOf("sh", "bash"),
            testingLanguageNames = emptySet(),
            testingFrameworks = setOf(Framework.BashIO)),
    Cpp(Names.CPP, setOf("cc", "cpp"),
            testingLanguageNames = setOf(Names.BASH)),
    C(Names.C, setOf("c")),
    Scala(Names.SCALA, setOf("scala")),
    R(Names.R, setOf("R")),
    Python(Names.PYTHON, setOf("py")),
    Haskell(Names.HASKELL, setOf("sk")),
    Javascript(Names.JAVASCRIPT, setOf("js")),
    Rust(Names.RUST, setOf("rs")),
    Pascal(Names.PASCAL, setOf("pas", "pp")),
    Fortran(Names.FORTRAN, setOf("f", "f90", "f95", "f03")),
    Lisp(Names.LISP, setOf("lisp", "cl")),
    Perl(Names.PERL, setOf("pl", "pm")),
    Matlab(Names.MATLAB, setOf("m")),
    Prolog(Names.PROLOG, setOf("pl", "pro")),
    PlSql(Names.PLSQL, setOf("sql", "pls"))
    ;

    /**
     * Compatible languages that can be used for writing tests for the language.
     */
    val testingLanguages: Set<Language>
        get() = testingLanguageNames.mapNotNull { from(it) }.toSet()

    override fun toString() = alias

    /**
     * Checks if the given [testingLanguage] can be used as testing language
     * for the current language.
     */
    infix fun canBeTestedBy(testingLanguage: Language): Boolean = testingLanguage in testingLanguages

    /**
     * Checks if the given [testingFramework] can be used as testing framework
     * by the current language.
     */
    infix fun worksWith(testingFramework: Framework): Boolean = testingFramework in testingFrameworks

    companion object {

        /**
         * Language name constants.
         */
        object Names {
            const val KOTLIN = "kotlin"
            const val JAVA = "java"
            const val BASH = "bash"
            const val CPP = "c++"
            const val C = "c"
            const val SCALA = "scala"
            const val R = "R"
            const val PYTHON = "python"
            const val HASKELL = "haskell"
            const val JAVASCRIPT = "javascript"
            const val RUST = "rust"
            const val PASCAL = "pascal"
            const val FORTRAN = "fortran"
            const val LISP = "lisp"
            const val PERL = "perl"
            const val MATLAB = "matlab"
            const val PROLOG = "prolog"
            const val PLSQL = "plsql"
        }

        /**
         * Returns an associated language instance.
         */
        fun from(alias: String?): Language? = if (alias == null) null else values().find { it.alias == alias }
    }
}
