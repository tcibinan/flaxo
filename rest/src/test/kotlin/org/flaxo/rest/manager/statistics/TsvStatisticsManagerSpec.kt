package org.flaxo.rest.manager.statistics

import org.jetbrains.spek.subject.SubjectSpek
import org.jetbrains.spek.subject.itBehavesLike

class TsvStatisticsManagerSpec : SubjectSpek<CsvStatisticsManager>({
    subject { TsvStatisticsManager() }

    itBehavesLike(CsvStatisticsManagerSpec)
})