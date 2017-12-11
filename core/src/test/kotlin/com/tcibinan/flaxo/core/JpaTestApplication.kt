package com.tcibinan.flaxo.core

import com.tcibinan.flaxo.core.dao.CourseRepository
import com.tcibinan.flaxo.core.dao.StudentRepository
import com.tcibinan.flaxo.core.dao.TaskRepository
import com.tcibinan.flaxo.core.dao.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
        scanBasePackages = ["com.tcibinan.flaxo"]
)
@EnableJpaRepositories("com.tcibinan.flaxo.core.dao")
@EntityScan("com.tcibinan.flaxo.core.model")
class JpaTestApplication {
    @Bean
    fun dataService(
            userRepository: UserRepository,
            courseRepository: CourseRepository,
            taskRepository: TaskRepository,
            studentRepository: StudentRepository
    ): DataService = BasicDataService(userRepository, courseRepository, taskRepository, studentRepository)
}