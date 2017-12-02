package com.tcibinan.flaxo.core.model

interface ConvertibleEntity<A> {
    fun toDto(): A
}

internal fun <A> Set<ConvertibleEntity<A>>.toDtos(): Set<A> = this.map { it.toDto() }.toSet()