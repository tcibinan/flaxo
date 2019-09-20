package org.flaxo.travis

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.flaxo.travis.webhook.SimpleTravisPullRequestBuild
import org.flaxo.travis.webhook.TravisWebHook
import java.io.Reader

/**
 * Travis web hook parser function.
 */
fun parseTravisWebHook(reader: Reader): TravisBuild? =
        ObjectMapper()
                .registerModules(KotlinModule(), JavaTimeModule())
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NONE)
                .readerFor(TravisWebHook::class.java)
                .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue<TravisWebHook>(reader)
                .let {
                    when (it.type) {
                        "pull_request" -> SimpleTravisPullRequestBuild(it)
                        else -> null
                    }
                }
