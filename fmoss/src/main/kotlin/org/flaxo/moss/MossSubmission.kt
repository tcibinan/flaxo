package org.flaxo.moss

import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.Language

/**
 * Moss analysis submission parameters.
 */
data class MossSubmission(

        val user: String,
        
        val course: String,

        val branch: String,

        /**
         * Plagiarism analysis target language.
         */
        val language: Language,

        /**
         * Original task local files.
         *
         * Files that was written by the teacher itself in the original task.
         */
        val base: List<LocalFile>,

        /**
         * Student solution local files.
         *
         * Files that was changed or created by the students.
         *
         * Each student files should have different root folder.
         *
         * F.e.
         *
         * Base files:
         * /some/common/path/base/SomeClass.java
         *
         * Student 1 files:
         * /some/common/path/student1/SomeClass.java
         * /some/common/path/student1/NewClass.java
         *
         * Student 2 files:
         * /some/common/path/student2/SomeClass.java
         */
        val solutions: List<LocalFile>
)
