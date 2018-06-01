package org.flaxo.rest.service.converter

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
     *
     * Map is supposed to be organised as the following example:
     *
     * {
     *   task1: {
     *     student1: 100,
     *     student2: 80
     *   },
     *   task2: {
     *     student1: 90,
     *     student2: 90
     *   }
     * }
     */
    fun convert(statistics: Map<String, Map<String, Int>>): String
}