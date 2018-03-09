package com.tcibinan.flaxo.model.entity

/**
 * Entity object interface.
 */
interface EntityObject<out DTO> {
    fun toDto(): DTO
}

internal fun <DTO> Set<EntityObject<DTO>>.toDtos(): Set<DTO> =
        this.map { it.toDto() }.toSet()