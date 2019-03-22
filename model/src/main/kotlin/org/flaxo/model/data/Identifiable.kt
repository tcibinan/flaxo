package org.flaxo.model.data

/**
 * Identifiable entity interface.
 */
@Deprecated("Should be replace with Identifiable from the common module", replaceWith = ReplaceWith("Identifiable", "org.flaxo.common.data.Identifiable"))
interface Identifiable {

    /**
     * Entity id.
     */
    val id: Long
}
