package org.flaxo.moss

import org.flaxo.common.Language

fun toGitplagLanguage(language: Language?) =
        when (language) {
            Language.C -> io.gitplag.gitplagapi.model.enums.Language.C
            Language.Cpp -> io.gitplag.gitplagapi.model.enums.Language.CPP
            Language.Fortran -> io.gitplag.gitplagapi.model.enums.Language.FORTRAN
            Language.Haskell -> io.gitplag.gitplagapi.model.enums.Language.HASKELL
            Language.Java -> io.gitplag.gitplagapi.model.enums.Language.JAVA
            Language.Javascript -> io.gitplag.gitplagapi.model.enums.Language.JAVASCRIPT
            Language.Lisp -> io.gitplag.gitplagapi.model.enums.Language.LISP
            Language.Matlab -> io.gitplag.gitplagapi.model.enums.Language.MATLAB
            Language.Pascal -> io.gitplag.gitplagapi.model.enums.Language.PASCAL
            Language.Perl -> io.gitplag.gitplagapi.model.enums.Language.PERL
            Language.PlSql -> io.gitplag.gitplagapi.model.enums.Language.PLSQL
            Language.Python -> io.gitplag.gitplagapi.model.enums.Language.PYTHON
            Language.Prolog -> io.gitplag.gitplagapi.model.enums.Language.PROLOG
            else -> throw RuntimeException()
        }