package org.flaxo.rest

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.moss.MossManager
import org.flaxo.rest.manager.plagiarism.BasicPlagiarismManager
import org.flaxo.rest.manager.plagiarism.PlagiarismManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PlagiarismConfiguration {

    @Bean
    fun plagiarismManager(dataManager: DataManager, mossManager: MossManager): PlagiarismManager =
            BasicPlagiarismManager(dataManager, mossManager)

}
