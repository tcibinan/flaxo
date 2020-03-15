package org.flaxo.rest

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.plag.BasicPlagiarismManager
import org.flaxo.rest.manager.plag.PlagiarismAnalyser
import org.flaxo.rest.manager.plag.PlagiarismAnalysisManager
import org.flaxo.rest.manager.plag.PlagiarismManager
import org.flaxo.rest.manager.plag.SimplePlagiarismAnalysisManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlagiarismConfiguration {

    @Bean
    fun plagiarismAnalysisManager(dataManager: DataManager,
                                  plagiarismAnalyser: PlagiarismAnalyser
    ): PlagiarismAnalysisManager = SimplePlagiarismAnalysisManager(dataManager, plagiarismAnalyser)

    @Bean
    fun plagiarismManager(dataManager: DataManager,
                          plagiarismAnalysisManager: PlagiarismAnalysisManager
    ): PlagiarismManager = BasicPlagiarismManager(dataManager, plagiarismAnalysisManager)
}
