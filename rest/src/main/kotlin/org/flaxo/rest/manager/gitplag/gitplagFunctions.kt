package org.flaxo.rest.manager.gitplag

import io.gitplag.gitplagapi.model.enums.AnalyzerProperty
import org.flaxo.common.Language
import org.flaxo.common.data.PlagiarismBackend
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
            else -> throw GitplagException("Language $language is not supported by Gitplag.")
        }

/**
 * Convert plagiarism backend to Gitplag analyzer.
 */
fun toGitplagAnalyser(backend: PlagiarismBackend): AnalyzerProperty? =
        when (backend) {
            PlagiarismBackend.MOSS -> AnalyzerProperty.MOSS
            PlagiarismBackend.JPLAG -> AnalyzerProperty.JPLAG
            PlagiarismBackend.COMBINED -> AnalyzerProperty.COMBINED
            else -> throw GitplagException("Plagiarism backend $backend is not supported by Gitplag.")
        }
