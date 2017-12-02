package com.tcibinan.flaxo.core.model

interface DataObject<T> {
    fun toEntity(): T
}

internal fun <A> Set<DataObject<A>>.toEntities(): Set<A> = this.map { it.toEntity() }.toSet()