package org.flaxo.rest.manager

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.course.BasicCourseManager
import org.flaxo.rest.manager.course.CourseManager
import org.flaxo.rest.manager.gitplag.GitplagManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CourseConfiguration {

    @Bean
    fun courseManager(dataManager: DataManager, gitplagManager: GitplagManager): CourseManager =
            BasicCourseManager(dataManager, gitplagManager)
}
