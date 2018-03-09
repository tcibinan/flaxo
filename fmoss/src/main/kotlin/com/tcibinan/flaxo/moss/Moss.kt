package com.tcibinan.flaxo.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile

/**
 * Moss plagiarism detection analysis client interface.
 */
interface Moss {

    /**
     * Moss client user id retrieved from the stanford official site.
     */
    val userId: String

    /**
     * Language plagiarism analysis will be performed on.
     */
    val language: String

    /**
     * Create moss client with the files that was written
     * by the teacher itself in the original state.
     *
     * @param bases Original task environment files.
     * @return Moss client with the list of bases environment files.
     */
    fun base(bases: List<EnvironmentFile>): Moss

    /**
     * Create moss client with the files that was written
     * by the students identified by the file names.
     *
     * Files for different students should have different root folder.
     * f.e. student1/SomeClass.java, student2/SomeClass.java.
     *
     * @param solutions Student solutions environment files.
     * @return Moss client with the list of solution environment files.
     */
    fun solutions(solutions: List<EnvironmentFile>): Moss

    /**
     * Start the moss plagiarism analysis.
     *
     * @return Result of the moss analysis.
     */
    fun analyse(): MossResult
}

