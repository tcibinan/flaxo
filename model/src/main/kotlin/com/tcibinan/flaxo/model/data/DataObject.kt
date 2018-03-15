package com.tcibinan.flaxo.model.data

import com.tcibinan.flaxo.model.EntityFieldIsAbsent

/**
 * Data object interface.
 */
interface DataObject<out ENTITY> {

    /**
     * Converts data object to entity object.
     */
    fun toEntity(): ENTITY

    /**
     * Returns view of the current data object.
     */
    fun view(): Any = object {
        val info: String = "View is not specified for ${this::class.simpleName} data object"
    }
}

fun DataObject<*>.missing(field: String): Nothing {
    throw EntityFieldIsAbsent(this::class, field)
}

fun Set<DataObject<*>>.toViews(): List<Any> = map { it.view() }

internal fun <ENTITY> Set<DataObject<ENTITY>>.toEntities(): Set<ENTITY> =
        this.map { it.toEntity() }.toSet()