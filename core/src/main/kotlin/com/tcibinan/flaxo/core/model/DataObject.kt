package com.tcibinan.flaxo.core.model

interface DataObject<ENTITY> {
    fun toEntity(): ENTITY
}

internal fun <ENTITY> Set<DataObject<ENTITY>>.toEntities(): Set<ENTITY> = this.map { it.toEntity() }.toSet()