package org.flaxo.rest;

import org.flaxo.rest.manager.github.GithubManager
import org.flaxo.rest.manager.notification.GitHubNotificationManager
import org.flaxo.rest.manager.notification.NotificationManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

/**
 * Notification configuration.
 */
@Configuration
@EnableAsync
class NotificationConfiguration {

    @Bean
    fun notificationManager(githubManager: GithubManager): NotificationManager =
            GitHubNotificationManager(githubManager)
}
