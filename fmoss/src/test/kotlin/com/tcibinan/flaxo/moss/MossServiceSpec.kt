package com.tcibinan.flaxo.moss

import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek

object MossServiceSpec : SubjectSpek<MossService>({
    val language = "java"
    val userId = "test_userid"

    subject { SimpleMossService(userId) }

    describe("moss service") {

        on("creating client for $language language with $userId userid") {
            val mossClient = subject.client(language)

            it("should client with userid = $userId") {
                mossClient.userId == userId
            }

            it("should client with language = $language") {
                mossClient.language == language
            }
        }
    }

})

