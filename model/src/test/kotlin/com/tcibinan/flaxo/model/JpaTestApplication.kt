package com.tcibinan.flaxo.model

import com.tcibinan.flaxo.model.dao.CourseRepository
import com.tcibinan.flaxo.model.dao.StudentRepository
import com.tcibinan.flaxo.model.dao.StudentTaskRepository
import com.tcibinan.flaxo.model.dao.TaskRepository
import com.tcibinan.flaxo.model.dao.UserRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
        scanBasePackages = ["com.tcibinan.flaxo"]
)
@EnableJpaRepositories("com.tcibinan.flaxo.model.dao")
@EntityScan("com.tcibinan.flaxo.model.entity")
class JpaTestApplication {
    @Bean
    fun dataService(userRepository: UserRepository,
                    courseRepository: CourseRepository,
                    taskRepository: TaskRepository,
                    studentRepository: StudentRepository,
                    studentTaskRepository: StudentTaskRepository
    ): DataService = BasicDataService(
            userRepository,
            courseRepository,
            taskRepository,
            studentRepository,
            studentTaskRepository
    )
}