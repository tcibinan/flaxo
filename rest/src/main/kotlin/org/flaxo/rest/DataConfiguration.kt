package org.flaxo.rest

import org.flaxo.model.PlainDataManager
import org.flaxo.model.DataManager
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
import org.flaxo.rest.manager.data.SecuredDataManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Data services configuration.
 */
@Configuration
class DataConfiguration {

    @Bean
    fun dataManager(userRepository: UserRepository,
                    credentialsRepository: CredentialsRepository,
                    courseRepository: CourseRepository,
                    taskRepository: TaskRepository,
                    studentRepository: StudentRepository,
                    solutionRepository: SolutionRepository,
                    buildReportRepository: BuildReportRepository,
                    codeStyleReportRepository: CodeStyleReportRepository,
                    plagiarismReportRepository: PlagiarismReportRepository,
                    commitRepository: CommitRepository,
                    passwordEncoder: PasswordEncoder
    ): DataManager =
            PlainDataManager(
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
            ).let {
                SecuredDataManager(it, passwordEncoder)
            }

}