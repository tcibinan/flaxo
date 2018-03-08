package com.tcibinan.flaxo.model.data

/**
 * Data object interface.
 */
interface DataObject<ENTITY> {
    fun toEntity(): ENTITY
}

internal fun <ENTITY> Set<DataObject<ENTITY>>.toEntities(): Set<ENTITY> =
        this.map { it.toEntity() }.toSet()