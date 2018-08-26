package org.flaxo.rest.manager.statistics

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
 * Course statistics manager.
 *
 * It is used to write course statistics to a different representations.
 */
interface StatisticsManager {

    /**
     * Statistics file extension.
     */
    val extension: String

    /**
     * Converts given statistics to a single string.
     */
    fun convert(statistics: Statistics): String
}