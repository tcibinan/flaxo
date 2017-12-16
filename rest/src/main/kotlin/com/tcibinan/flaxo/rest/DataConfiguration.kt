package com.tcibinan.flaxo.rest

import com.tcibinan.flaxo.core.BasicDataService
import com.tcibinan.flaxo.core.DataService
import com.tcibinan.flaxo.core.dao.CourseRepository
import com.tcibinan.flaxo.core.dao.StudentRepository
import com.tcibinan.flaxo.core.dao.StudentTaskRepository
import com.tcibinan.flaxo.core.dao.TaskRepository
import com.tcibinan.flaxo.core.dao.UserRepository
import com.tcibinan.flaxo.rest.security.SecuredDataService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class DataConfiguration {

    @Bean
    fun nonSecuredDataService(
            userRepository: UserRepository,
            courseRepository: CourseRepository,
            taskRepository: TaskRepository,
            studentRepository: StudentRepository,
            studentTaskRepository: StudentTaskRepository
    ): DataService =
            BasicDataService(userRepository, courseRepository, taskRepository, studentRepository, studentTaskRepository)

    @Bean
    fun dataService(
            nonSecuredDataService: DataService,
            passwordEncoder: PasswordEncoder
    ): DataService =
            SecuredDataService(nonSecuredDataService, passwordEncoder)

}