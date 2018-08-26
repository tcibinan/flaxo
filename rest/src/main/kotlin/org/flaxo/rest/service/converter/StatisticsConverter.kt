package org.flaxo.rest.service.converter

/**
 * Course statistics summary supposed to be organised as the following:
 * {
 *   "task1": {
 *     "student1": 100,
 *     "student2": 80
 *   },
 *   "task2": {
 *     "student1": 90,
 *     "student2": 90
 *   }
 * }
 */
typealias Statistics = Map<String, Map<String, Int>>

/**
 * Interface of course statistics converter.
 *
 * It is used to form a course statistics to a different
 * file formats.
 */
interface StatisticsConverter {

    /**
     * Statistics file extension.
     */
    val extension: String

    /**
     * Converts given statistics to a single string.
     */
    fun convert(statistics: Statistics): String
}