package org.flaxo.rest.manager.gitplag

import org.flaxo.common.Language
import org.flaxo.rest.manager.UnsupportedLanguageException
import io.gitplag.gitplagapi.model.enums.Language as GitplagLanguage

/**
 * Convert Flaxo language to Gitplag language.
 */
fun toGitplagLanguage(language: Language?) =
        when (language) {
            Language.C -> GitplagLanguage.C
            Language.Cpp -> GitplagLanguage.CPP
            Language.Fortran -> GitplagLanguage.FORTRAN
            Language.Haskell -> GitplagLanguage.HASKELL
            Language.Java -> GitplagLanguage.JAVA
            Language.Javascript -> GitplagLanguage.JAVASCRIPT
            Language.Lisp -> GitplagLanguage.LISP
            Language.Matlab -> GitplagLanguage.MATLAB
            Language.Pascal -> GitplagLanguage.PASCAL
            Language.Perl -> GitplagLanguage.PERL
            Language.PlSql -> GitplagLanguage.PLSQL
            Language.Python -> GitplagLanguage.PYTHON
            Language.Prolog -> GitplagLanguage.PROLOG
            else -> throw UnsupportedLanguageException("Language $language is not supported by Gitplag.")
        }