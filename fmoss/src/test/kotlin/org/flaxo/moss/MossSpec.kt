package org.flaxo.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import it.zielke.moji.SocketClient
import org.flaxo.core.env.file.LocalEnvironmentFile
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.JavaLang
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.net.URL
import kotlin.test.assertTrue

object MossSpec : SubjectSpek<Moss>({
    val user = "user"
    val course = "course"
    val branch = "branch"
    val language = JavaLang
    val base: List<LocalFile> = listOf(
            LocalEnvironmentFile("src/test/resources/base/ClassName.java")
    )
    val solutions: List<LocalFile> = listOf(
            LocalEnvironmentFile("src/test/resources/student1/ClassName.java"),
            LocalEnvironmentFile("src/test/resources/student2/ClassName.java"),
            LocalEnvironmentFile("src/test/resources/student3/ClassName.java")
    )
    val submission = MossSubmission(user, course, branch, language, base, solutions)
    val client: SocketClient = mock {
        on { resultURL }.thenReturn(URL("http://test.url.com/results/2312432"))
    }

    subject { SimpleMoss.using(client) }

    describe("moss client") {

        on("analysing directory with files") {
            val result = subject.analyse(submission)

            it("should add all base files") {
                verify(client, times(base.size)).uploadFile(any(), eq(true))
            }

            it("should add all students files") {
                verify(client, times(solutions.size)).uploadFile(any(), eq(false))
            }

            it("should generate a result object with non-blank url") {
                assertTrue("Moss test results is available here: ${result.url}") {
                    result.url.toString().isNotBlank()
                }
            }
        }
    }
})
