package com.tcibinan.flaxo.model.entity

interface ConvertibleEntity<DTO> {
    fun toDto(): DTO
}

internal fun <DTO> Set<ConvertibleEntity<DTO>>.toDtos(): Set<DTO> =
        this.map { it.toDto() }.toSet()