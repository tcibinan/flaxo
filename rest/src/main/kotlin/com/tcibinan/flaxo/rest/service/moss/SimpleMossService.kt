package com.tcibinan.flaxo.rest.service.moss

import com.tcibinan.flaxo.core.env.EnvironmentFile
import com.tcibinan.flaxo.core.language.Language
import com.tcibinan.flaxo.model.data.Course
import com.tcibinan.flaxo.moss.Moss
import com.tcibinan.flaxo.moss.SimpleMoss
import com.tcibinan.flaxo.rest.service.git.GitService
import it.zielke.moji.SocketClient

class SimpleMossService(private val userId: String,
                        private val gitService: GitService,
                        private val supportedLanguages: Map<String, Language>
) : MossService {

    override fun client(language: String): Moss =
            SimpleMoss(userId, language, SocketClient())

    override fun extractMossTasks(course: Course): Set<MossTask> {
        val user = course.user

        val githubToken = user.credentials.githubToken
                ?: throw Exception("Github credentials wasn't found for user ${user.nickname}.")

        val userGithubId = user.githubId
                ?: throw Exception("Github id for user ${user.nickname} wasn't found.")

        val git = gitService.with(githubToken)

        val language = supportedLanguages[course.language]
                ?: throw Exception("Language ${course.language} is not supported for analysis yet.")


        val studentsSolutionsFiles = course.students
                .map { student ->
                    student.nickname to
                            student.studentTasks
                                    .filter { it.anyBuilds }
                                    .filter { it.buildSucceed }
                                    .map { it.task.name }
                }
                .map { (student, solvedTasks) ->
                    student to
                            git.branches(student, course.name)
                                    .filter { branch -> branch.name() in solvedTasks }
                }
                .flatMap { (student, branches) ->
                    branches.map { student to it }
                }
                .groupBy { (_, branch) -> branch.name() }
                .mapValues { (_, solutions) ->
                    solutions.map { (student, branch) ->
                        student to branch.files().withLanguageExtension(language)
                    }
                }

        val baseFiles =
                git.branches(userGithubId, course.name)
                        .map { it.name() to it.files().withLanguageExtension(language) }
                        .filter { (task, _) ->
                            task in studentsSolutionsFiles.keys
                        }

        // TODO: 23/02/18 Cut files same prefix

        return baseFiles
                .map { (branch, files) ->
                    files to
                            studentsSolutionsFiles[branch].orEmpty()
                                    .flatMap {
                                        // TODO: 23/02/18 Add root folder with student name to file path
                                        it.second
                                    }
                                    .toSet()
                }
                .map { (base, solutions) -> MossTask(base, solutions) }
                .toSet()
    }

}

private fun Set<EnvironmentFile>.withLanguageExtension(language: Language): Set<EnvironmentFile> =
        filter { it.name().endsWith(language.extension) }.toSet()

