package org.flaxo.rest.manager.moss

import org.flaxo.model.data.Task
import org.flaxo.moss.MossSubmission

/**
 * Moss submissions extractor.
 */
interface MossSubmissionExtractor {

    /**
     * Extract moss submission for [task] using owner and student's repository branches.
     *
     * @return Submission with files that have valid extensions in terms of the [task]'s course language.
     */
    fun extract(task: Task): MossSubmission
}
