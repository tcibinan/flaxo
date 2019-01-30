package org.flaxo.model

import org.flaxo.model.dao.BuildReportRepository
import org.flaxo.model.dao.CodeStyleReportRepository
import org.flaxo.model.dao.CommitRepository
import org.flaxo.model.dao.CourseRepository
import org.flaxo.model.dao.SettingsRepository
import org.flaxo.model.dao.CredentialsRepository
import org.flaxo.model.dao.PlagiarismReportRepository
import org.flaxo.model.dao.StudentRepository
import org.flaxo.model.dao.SolutionRepository
import org.flaxo.model.dao.TaskRepository
import org.flaxo.model.dao.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["org.flaxo"])
@EnableJpaRepositories("org.flaxo.model.dao")
@EntityScan("org.flaxo.model.data")
class JpaTestApplication {

    @Bean
    fun dataService(userRepository: UserRepository,
                    credentialsRepository: CredentialsRepository,
                    courseRepository: CourseRepository,
                    settingsRepository: SettingsRepository,
                    taskRepository: TaskRepository,
                    studentRepository: StudentRepository,
                    solutionRepository: SolutionRepository,
                    buildReportRepository: BuildReportRepository,
                    codeStyleReportRepository: CodeStyleReportRepository,
                    plagiarismReportRepository: PlagiarismReportRepository,
                    commitRepository: CommitRepository
    ): DataManager = PlainDataManager(
            userRepository,
            credentialsRepository,
            courseRepository,
            settingsRepository,
            taskRepository,
            studentRepository,
            solutionRepository,
            buildReportRepository,
            codeStyleReportRepository,
            plagiarismReportRepository,
            commitRepository
    )
}