package org.flaxo.model.data

/**
 * Interface describing a data object that can be displayed as a simple json.
 */
interface Viewable {

    /**
     * Returns view of the current data object.
     */
    fun view(): Any = object {
        val info: String = "View is not specified for ${this::class.simpleName} data object"
    }
}

/**
 * Returns views of the collection elements.
 */
fun Collection<Viewable>.views(): List<Any> = map { it.view() }