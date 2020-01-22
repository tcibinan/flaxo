package org.flaxo.rest.manager

import org.flaxo.model.DataManager
import org.flaxo.rest.manager.course.BasicCourseManager
import org.flaxo.rest.manager.course.CourseManager
import org.flaxo.rest.manager.gitplag.GitplagCourseManager
import org.flaxo.rest.manager.gitplag.GitplagManager
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CourseConfiguration {

    @Bean
    @ConditionalOnProperty(name = ["flaxo.plagiarism.analyser"], havingValue = "moss")
    fun basicCourseManager(dataManager: DataManager): CourseManager = BasicCourseManager(dataManager)

    @Bean
    @ConditionalOnProperty(name = ["flaxo.plagiarism.analyser"], havingValue = "gitplag")
    fun gitplagCourseManager(dataManager: DataManager, gitplagManager: GitplagManager): CourseManager =
            GitplagCourseManager(BasicCourseManager(dataManager), dataManager, gitplagManager)
}
