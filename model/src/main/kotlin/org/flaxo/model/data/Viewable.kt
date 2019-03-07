package org.flaxo.model.data

/**
 * Data object that can be transformed to an instance of [VIEW] class.
 */
interface Viewable<VIEW> {

    /**
     * Returns view of the current data object.
     */
    fun view(): VIEW
}

/**
 * Returns views of the data objects collection.
 */
fun <VIEW> Collection<Viewable<VIEW>>.views(): List<VIEW> = map { it.view() }
