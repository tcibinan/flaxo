package org.flaxo.model.data

/**
 * Interface describing a data object that can be displayed as simple json.
 */
interface Viewable {

    /**
     * Returns view of the current data object.
     */
    fun view(): Any = object {
        val info: String = "View is not specified for ${this::class.simpleName} data object"
    }
}

fun Set<Viewable>.toViews(): List<Any> = map { it.view() }