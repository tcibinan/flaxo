package org.flaxo.common.data.plagiarism

// TODO 18.03.19: Replace with import from data2graph.
/**
 * Graph link.
 *
 * Represents a weighed connection between two graph nodes.
 *
 * Graph link is bidirectional.
 */
data class PlagiarismLink(

        /**
         * First of the link's nodes name.
         */
        val first: String,

        /**
         * Second of the link's nodes name.
         */
        val second: String,

        /**
         * Link weight.
         *
         * Represents the nodes closeness value. It lies *between 0 and 100*.
         *
         * *The higher weight the closer nodes are.*
         */
        val weight: Int
)
