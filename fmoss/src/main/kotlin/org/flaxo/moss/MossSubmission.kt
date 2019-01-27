package org.flaxo.moss

import org.flaxo.common.env.file.LocalFile
import org.flaxo.common.lang.Language
import java.nio.file.Path

/**
 * Moss analysis submission parameters.
 */
data class MossSubmission(

        val user: String,
        
        val course: String,

        val task: String,

        /**
         * Plagiarism analysis target language.
         */
        val language: Language,

        /**
         * Students which solutions are going to be analysed.
         */
        val students: List<String>,

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
        val solutions: List<LocalFile>,

        /**
         * Directory that contains all files supposed to **be deleted** when submission finishes.
         */
        val tempDirectory: Path
)
