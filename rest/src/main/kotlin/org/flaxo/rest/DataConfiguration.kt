package org.flaxo.rest

import org.flaxo.model.BasicDataService
import org.flaxo.model.DataService
import org.flaxo.model.dao.BuildReportRepository
import org.flaxo.model.dao.CodeStyleReportRepository
import org.flaxo.model.dao.CommitRepository
import org.flaxo.model.dao.CourseRepository
import org.flaxo.model.dao.CredentialsRepository
import org.flaxo.model.dao.PlagiarismReportRepository
import org.flaxo.model.dao.StudentRepository
import org.flaxo.model.dao.SolutionRepository
import org.flaxo.model.dao.TaskRepository
import org.flaxo.model.dao.UserRepository
import org.flaxo.rest.service.data.SecuredDataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Data services configuration.
 */
@Configuration
class DataConfiguration {

    @Bean
    fun nonSecuredDataService(userRepository: UserRepository,
                              credentialsRepository: CredentialsRepository,
                              courseRepository: CourseRepository,
                              taskRepository: TaskRepository,
                              studentRepository: StudentRepository,
                              solutionRepository: SolutionRepository,
                              buildReportRepository: BuildReportRepository,
                              codeStyleReportRepository: CodeStyleReportRepository,
                              plagiarismReportRepository: PlagiarismReportRepository,
                              commitRepository: CommitRepository
    ): DataService = BasicDataService(
            userRepository,
            credentialsRepository,
            courseRepository,
            taskRepository,
            studentRepository,
            solutionRepository,
            buildReportRepository,
            codeStyleReportRepository,
            plagiarismReportRepository,
            commitRepository
    )

    @Bean
    fun dataService(nonSecuredDataService: DataService,
                    passwordEncoder: PasswordEncoder
    ): DataService = SecuredDataService(
            nonSecuredDataService,
            passwordEncoder
    )

}