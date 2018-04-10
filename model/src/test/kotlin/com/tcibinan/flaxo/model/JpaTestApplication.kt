package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.dao.CourseRepository
import com.tcibinan.flaxo.model.dao.CredentialsRepository
import com.tcibinan.flaxo.model.dao.StudentRepository
import com.tcibinan.flaxo.model.dao.SolutionRepository
import com.tcibinan.flaxo.model.dao.TaskRepository
import com.tcibinan.flaxo.model.dao.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.tcibinan.flaxo"])
@EnableJpaRepositories("com.tcibinan.flaxo.model.dao")
@EntityScan("com.tcibinan.flaxo.model.data")
class JpaTestApplication {
    @Bean
    fun dataService(userRepository: UserRepository,
                    credentialsRepository: CredentialsRepository,
                    courseRepository: CourseRepository,
                    taskRepository: TaskRepository,
                    studentRepository: StudentRepository,
                    solutionRepository: SolutionRepository
    ): DataService = BasicDataService(
            userRepository,
            credentialsRepository,
            courseRepository,
            taskRepository,
            studentRepository,
            solutionRepository
    )
}