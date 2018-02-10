package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.model.BasicDataService
import com.tcibinan.flaxo.model.DataService
import com.tcibinan.flaxo.model.dao.CourseRepository
import com.tcibinan.flaxo.model.dao.CredentialsRepository
import com.tcibinan.flaxo.model.dao.StudentRepository
import com.tcibinan.flaxo.model.dao.StudentTaskRepository
import com.tcibinan.flaxo.model.dao.TaskRepository
import com.tcibinan.flaxo.model.dao.UserRepository
import com.tcibinan.flaxo.rest.service.data.SecuredDataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataConfiguration {

    @Bean
    fun nonSecuredDataService(
            userRepository: UserRepository,
            credentialsRepository: CredentialsRepository,
            courseRepository: CourseRepository,
            taskRepository: TaskRepository,
            studentRepository: StudentRepository,
            studentTaskRepository: StudentTaskRepository
    ): DataService =
            BasicDataService(
                    userRepository,
                    credentialsRepository,
                    courseRepository,
                    taskRepository,
                    studentRepository,
                    studentTaskRepository
            )

    @Bean
    fun dataService(
            nonSecuredDataService: DataService,
            passwordEncoder: PasswordEncoder
    ): DataService =
            SecuredDataService(
                    nonSecuredDataService,
                    passwordEncoder
            )

}