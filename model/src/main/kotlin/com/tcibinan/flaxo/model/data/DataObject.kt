package com.tcibinan.flaxo.model.data

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

internal fun <ENTITY> Set<DataObject<ENTITY>>.toEntities(): Set<ENTITY> =
        this.map { it.toEntity() }.toSet()