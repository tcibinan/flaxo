package org.flaxo.common.data.plagiarism

/**
 * Graph - a set of nodes and links that connects some of the given nodes.
 */
data class PlagiarismGraph(

        /**
         * Graph nodes.
         */
        val nodes: List<PlagiarismNode> = emptyList(),

        /**
         * Graph links.
         */
        val links: List<PlagiarismLink> = emptyList()
)
