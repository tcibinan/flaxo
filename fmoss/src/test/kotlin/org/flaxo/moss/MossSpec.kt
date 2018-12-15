package org.flaxo.moss

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import it.zielke.moji.SocketClient
import org.amshove.kluent.shouldEqual
import org.flaxo.core.env.file.LocalEnvironmentFile
import org.flaxo.core.env.file.LocalFile
import org.flaxo.core.lang.JavaLang
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.net.URL

object MossSpec : SubjectSpek<Moss>({
    val mossResultUrl = URL("http://test.url.com/results/2312432")
    val base: List<LocalFile> = listOf(
            LocalEnvironmentFile("src/test/resources/base/ClassName.java")
    )
    val solutions: List<LocalFile> = listOf(
            LocalEnvironmentFile("src/test/resources/student1/ClassName.java"),
            LocalEnvironmentFile("src/test/resources/student2/ClassName.java"),
            LocalEnvironmentFile("src/test/resources/student3/ClassName.java")
    )
    val submission = MossSubmission("user", "course", "branch", JavaLang, base, solutions)
    val client: SocketClient = mock {
        on { resultURL }.thenReturn(mossResultUrl)
    }

    subject { SimpleMoss.using(client) }

    describe("Moss client") {

        on("analysing directory with files") {
            val result = subject.submit(submission)

            it("should add all base files") {
                verify(client, times(base.size)).uploadFile(any(), eq(true))
            }

            it("should add all students files") {
                verify(client, times(solutions.size)).uploadFile(any(), eq(false))
            }

            it("should return actual Moss analysis url") {
                result shouldEqual mossResultUrl
            }
        }
    }
})
