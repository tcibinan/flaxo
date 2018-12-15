package org.flaxo.moss

import java.net.URL

/**
 * Moss plagiarism detection analysis result.
 */
class MossResult(

        /**
         * Moss platform result url.
         */
        val url: URL,

        /**
         * All Moss plagiarism matches.
         */
        val matches: Set<MossMatch>
)
