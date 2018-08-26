package org.flaxo.rest.manager.statistics

/**
 * Course statistics tsv manager.
 */
class TsvStatisticsManager : CsvStatisticsManager(delimiter = "\t", extension = "tsv")