package com.tcibinan.flaxo.core.model

interface ConvertibleEntity<A> {
    fun toDto(): A
}